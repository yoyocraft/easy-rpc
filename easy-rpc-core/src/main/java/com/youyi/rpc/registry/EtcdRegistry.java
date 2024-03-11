package com.youyi.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

/**
 * etcd 注册中心
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class EtcdRegistry implements Registry {

    /**
     * 服务注册根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 服务节点注册 TTL
     */
    private static final long SERVICE_TTL = 30L;

    /**
     * 本机注册的节点 Key 集合，用于维护续期
     */
    private final Set<String> LOCAL_REGISTERED_NODE_KEY_SET = new HashSet<>();

    /**
     * 注册中心服务节点元信息缓存
     */
    private final RegistryServiceCache REGISTRY_SERVICE_CACHE = new RegistryServiceCache();

    /**
     * 正在监听的 Key 集合
     */
    private final Set<String> WATCHING_KEY_SET = new ConcurrentHashSet<>();

    private Client client;

    private KV kvClient;

    @Override
    public void init(RegistryConfig config) {
        client = Client.builder()
                .endpoints(config.getEndpoints())
                .connectTimeout(Duration.ofMillis(config.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetadata metadata) throws Exception {
        // 创建 key 并设置过期时间， value 为服务注册信息的 JSON 序列化
        Lease leaseClient = client.getLeaseClient();

        // 30s 的租约
        long leaseId = leaseClient.grant(SERVICE_TTL).get().getID();

        // 存储的键值对
        String regKey = ETCD_ROOT_PATH + metadata.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(regKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(metadata),
                StandardCharsets.UTF_8);

        // 绑定键值对和租约
        PutOption putOp = PutOption.builder().withLeaseId(leaseId).build();

        // 注册到注册中心和本地
        kvClient.put(key, value, putOp).get();
        LOCAL_REGISTERED_NODE_KEY_SET.add(regKey);
    }

    @Override
    public void deregister(ServiceMetadata metadata) {
        String regKey = ETCD_ROOT_PATH + metadata.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(regKey, StandardCharsets.UTF_8));
        LOCAL_REGISTERED_NODE_KEY_SET.remove(regKey);
    }

    @Override
    public List<ServiceMetadata> discovery(String serviceKey) {

        // 读取缓存
        List<ServiceMetadata> serviceMetadataCache = REGISTRY_SERVICE_CACHE.read();
        if (CollUtil.isNotEmpty(serviceMetadataCache)) {
            log.info("read service metadata from local cache[REGISTRY_SERVICE_CACHE]!");
            return serviceMetadataCache;
        }

        // 前缀搜索时，需要加上结尾的 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOp = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValueList = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOp).get().getKvs();
            // 解析服务信息
            List<ServiceMetadata> serviceMetadataList = keyValueList.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 的变化
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetadata.class);
                    })
                    .toList();
            // 写入缓存
            log.info("write service metadata into local cache[REGISTRY_SERVICE_CACHE]!");
            REGISTRY_SERVICE_CACHE.write(serviceMetadataList);
            return serviceMetadataList;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("service discovery error, ", e);
        }
    }

    @Override
    public void watch(String serviceNodeKey) {
        boolean toBeWatching = WATCHING_KEY_SET.add(serviceNodeKey);
        if (!toBeWatching) {
            return;
        }

        // 之前未监听，开启监听
        Watch watchClient = client.getWatchClient();
        watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8),
                response -> {
                    List<WatchEvent> events = response.getEvents();
                    for (WatchEvent event : events) {
                        // key 删除时触发，清理注册服务缓存
                        if (WatchEvent.EventType.DELETE.equals(event.getEventType())) {
                            log.info("{} is offline, clear cache", serviceNodeKey);
                            REGISTRY_SERVICE_CACHE.clear();
                        }
                    }
                });
    }

    @Override
    public void heartBeat() {
        // 每 10s 续签一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String regKey : LOCAL_REGISTERED_NODE_KEY_SET) {
                try {
                    List<KeyValue> keyValueList = kvClient.get(
                            ByteSequence.from(regKey, StandardCharsets.UTF_8)).get().getKvs();
                    if (CollUtil.isEmpty(keyValueList)) {
                        // 该节点已过期（需要重启节点才可以重新注册）
                        continue;
                    }

                    // 节点未过期，重新注册
                    // actually keyValueList.size() == 1
                    String value = keyValueList.get(0).getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetadata metadata = JSONUtil.toBean(value, ServiceMetadata.class);
                    register(metadata);

                } catch (Exception e) {
                    throw new RuntimeException(regKey + " reset ttl failed", e);
                }
            }
        });

        // 设置秒匹配
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void destroy() {
        log.info("current registry node is destroying");
        // 下线节点
        for (String regKey : LOCAL_REGISTERED_NODE_KEY_SET) {
            try {
                kvClient.delete(ByteSequence.from(regKey, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(regKey + " offline failed", e);
            }
        }
        // 关闭连接
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}

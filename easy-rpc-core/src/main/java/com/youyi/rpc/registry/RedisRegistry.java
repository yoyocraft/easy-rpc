package com.youyi.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.util.MetadataUtil;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class RedisRegistry implements Registry {

    /**
     * Redis 注册中心根节点
     */
    private static final String REDIS_ROOT_PATH = "rpc:registry:";

    /**
     * 本机注册的节点 Key 集合，用于维护续期
     */
    private static final Set<String> LOCAL_REGISTERED_NODE_KEY_SET = new HashSet<>();

    /**
     * 注册中心服务节点元信息缓存
     */
    private static final RegistryServiceCache REGISTRY_SERVICE_CACHE = new RegistryServiceCache();

    /**
     * 正在监听的 Key 集合
     */
    private static final Set<String> WATCHING_KEY_SET = new ConcurrentHashSet<>();

    private Jedis jedis;


    @Override
    public void init(RegistryConfig config) {
        // TODO 支持 Redis 集群
        jedis = new Jedis(config.getEndpoints());
        jedis.auth(config.getPassword());
        String ping = jedis.ping();
        if (!"PONG".equals(ping)) {
            throw new IllegalStateException("Redis connection failed");
        }
    }

    @Override
    public void register(ServiceMetadata metadata) throws Exception {
        String nodeKey = MetadataUtil.getListServiceNodeKey(metadata);
        String regKey = REDIS_ROOT_PATH + nodeKey;
        jedis.set(regKey, JSONUtil.toJsonStr(metadata));
        jedis.expire(regKey, 100L);
        LOCAL_REGISTERED_NODE_KEY_SET.add(regKey);
    }

    @Override
    public void unregister(ServiceMetadata metadata) {
        String nodeKey = MetadataUtil.getListServiceNodeKey(metadata);
        String regKey = REDIS_ROOT_PATH + nodeKey;
        jedis.unlink(regKey);
        LOCAL_REGISTERED_NODE_KEY_SET.remove(regKey);
    }

    @Override
    public List<ServiceMetadata> discovery(String serviceKey) {
        // 读取缓存
        List<ServiceMetadata> serviceMetadataCache = REGISTRY_SERVICE_CACHE.read(serviceKey);
        if (CollUtil.isNotEmpty(serviceMetadataCache)) {
            log.info("read service metadata from local cache[REGISTRY_SERVICE_CACHE]!");
            return serviceMetadataCache;
        }
        String keyPattern = serviceKey + ":*";
        Set<String> keys = jedis.keys(keyPattern);
        List<String> jsonList = jedis.mget(keys.toArray(new String[0]));
        List<ServiceMetadata> serviceMetadataList = jsonList.stream()
                .map(json -> JSONUtil.toBean(json, ServiceMetadata.class)).toList();
        // 写入缓存
        REGISTRY_SERVICE_CACHE.write(serviceKey, serviceMetadataList);
        return serviceMetadataList;
    }

    @Override
    public void watch(String serviceNodeKey) {
        // 实现起来成本较高，暂不实现
    }

    @Override
    public void heartBeat() {
        // 每 10s 续签一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            Iterator<String> iterator = LOCAL_REGISTERED_NODE_KEY_SET.iterator();
            while (iterator.hasNext()) {
                String regKey = iterator.next();
                try {
                    String value = jedis.get(regKey);
                    if (value.isEmpty()) {
                        log.debug("{} is offline", regKey);
                        // 该节点已过期（需要重启节点才可以重新注册）
                        iterator.remove();
                        continue;
                    }
                    // 节点未过期，重新注册
                    // actually keyValueList.size() == 1
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
        if (jedis != null) {
            jedis.close();
        }
    }
}

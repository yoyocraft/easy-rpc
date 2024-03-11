package com.youyi.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
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

    private static final long SERVICE_TTL = 30L;

    private Client client;

    private KV kvClient;

    @Override
    public void init(RegistryConfig config) {
        client = Client.builder()
                .endpoints(config.getEndpoints())
                .connectTimeout(Duration.ofMillis(config.getTimeout()))
                .build();
        kvClient = client.getKVClient();
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
        kvClient.put(key, value, putOp).get();
    }

    @Override
    public void deregister(ServiceMetadata metadata) {
        String regKey = ETCD_ROOT_PATH + metadata.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(regKey, StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetadata> discovery(String serviceKey) {
        // 前缀搜索时，需要加上结尾的 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOp = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValueList = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOp).get().getKvs();
            // 解析服务信息
            return keyValueList.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetadata.class);
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("service discovery error, ", e);
        }
    }

    @Override
    public void destroy() {
        log.info("current registry node is destroying");
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}

package com.youyi.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.exception.RpcException;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.util.MetadataUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * zookeeper 注册中心
 * </p>
 * <a href="https://curator.apache.org/docs/getting-started">Apache Curator 操作文档</a>
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class ZookeeperRegistry implements Registry {

    /**
     * 服务注册根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";

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

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetadata> serviceDiscovery;

    @Override
    public void init(RegistryConfig config) {
        // 构建 client 实例
        client = CuratorFrameworkFactory
                .builder()
                .connectString(config.getEndpoints())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(config.getTimeout()), 3))
                .build();
        // 构建 serviceDiscovery 实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetadata.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetadata.class))
                .build();

        try {
            // 启动 client 和 serviceDiscovery
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RpcException("zk start error, ", e);
        }
    }

    @Override
    public void register(ServiceMetadata metadata) throws Exception {
        // 注册到 zk
        serviceDiscovery.registerService(buildServiceInstance(metadata));

        // 添加节点信息到本地缓存
        String regKey = ZK_ROOT_PATH + "/" + MetadataUtil.getServiceNodeKey(metadata);
        LOCAL_REGISTERED_NODE_KEY_SET.add(regKey);
    }


    @Override
    public void unregister(ServiceMetadata metadata) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(metadata));
        } catch (Exception e) {
            throw new RpcException(
                    "metadata" + MetadataUtil.getServiceNodeKey(metadata) + " unregister error");
        }
        String regKey = ZK_ROOT_PATH + "/" + MetadataUtil.getServiceNodeKey(metadata);
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

        try {
            // 查询服务信息
            Collection<ServiceInstance<ServiceMetadata>> serviceInstances
                    = serviceDiscovery.queryForInstances(serviceKey);

            // 解析服务信息
            List<ServiceMetadata> serviceMetadataList = serviceInstances.stream()
                    .map(ServiceInstance::getPayload)
                    .toList();

            // 写入服务缓存
            REGISTRY_SERVICE_CACHE.write(serviceKey, serviceMetadataList);

            return serviceMetadataList;
        } catch (Exception e) {
            throw new RpcException("get service instance failed, ", e);
        }
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean toBeWatching = WATCHING_KEY_SET.add(watchKey);
        if (!toBeWatching) {
            return;
        }
        String serviceKey = MetadataUtil.getServiceKey(serviceNodeKey);
        CuratorCache curatorCache = CuratorCache.build(client, watchKey);
        curatorCache.start();
        curatorCache.listenable().addListener(
                CuratorCacheListener
                        .builder()
                        .forDeletes(childData -> REGISTRY_SERVICE_CACHE.clear(serviceKey))
                        .forChanges((oldNode, newNode) -> REGISTRY_SERVICE_CACHE.clear(serviceKey))
                        .build()
        );

    }

    @Override
    public void heartBeat() {
        // 无需心跳机制，建立了临时节点，如果服务器故障，则临时节点直接丢失
    }

    @Override
    public void destroy() {
        log.info("current registry node is destroying");
        // 下线节点（可选，因为都是临时节点，服务下线，自然就删除了）
        for (String regKey : LOCAL_REGISTERED_NODE_KEY_SET) {
            try {
                client.delete().guaranteed().forPath(regKey);
            } catch (Exception e) {
                throw new RpcException(regKey + " offline failed!");
            }
        }
        if (client != null) {
            client.close();
        }
    }

    private ServiceInstance<ServiceMetadata> buildServiceInstance(ServiceMetadata metadata) {
        String addr = metadata.getServiceHost() + ":" + metadata.getServicePort();
        try {
            return ServiceInstance
                    .<ServiceMetadata>builder()
                    .id(addr)
                    .name(MetadataUtil.getServiceKey(metadata))
                    .address(addr)
                    .payload(metadata)
                    .build();
        } catch (Exception e) {
            throw new RpcException("build service instance error, ", e);
        }
    }
}

package com.youyi.rpc.registry;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.youyi.rpc.model.ServiceMetadata;
import java.time.Duration;
import java.util.List;

/**
 * 注册中心服务本地缓存
 * <p>
 * 正常情况下，服务节点信息列表的更新频率是不高的，所以可以缓存
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    private final Cache<String /* serviceKey */, List<ServiceMetadata>> serviceCache
            = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(1))
            .maximumSize(1024)
            .build();

    /**
     * 写缓存
     *
     * @param cache service metadata cache
     */
    void write(String serviceKey, List<ServiceMetadata> cache) {
        serviceCache.put(serviceKey, cache);
    }

    /**
     * 读缓存
     *
     * @return local service metadata cache
     */
    List<ServiceMetadata> read(String serviceKey) {
        return serviceCache.getIfPresent(serviceKey);
    }

    /**
     * 清理缓存
     */
    void clear(String serviceKey) {
        serviceCache.invalidate(serviceKey);
    }
}

package com.youyi.rpc.registry;

import com.youyi.rpc.model.ServiceMetadata;
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
    private List<ServiceMetadata> serviceCache;

    /**
     * 写缓存
     *
     * @param cache service metadata cache
     */
    void write(List<ServiceMetadata> cache) {
        this.serviceCache = cache;
    }

    /**
     * 读缓存
     *
     * @return local service metadata cache
     */
    List<ServiceMetadata> read() {
        return serviceCache;
    }

    /**
     * 清理缓存
     */
    void clear() {
        serviceCache = null;
    }
}

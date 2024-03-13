package com.youyi.rpc.lb;

import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class LoadBalancerFactory {

    /**
     * 获取负载均衡器
     *
     * @param key key
     * @return load balancer
     */
    public static LoadBalancer getLoadBalancer(String key) {
        LoadBalancer loadBalancer;
        try {
            loadBalancer = SpiLoader.getInstance(LoadBalancer.class, key);
        } catch (NoSuchLoadClassException e) {
            init();
            loadBalancer = SpiLoader.getInstance(LoadBalancer.class, key);
        }
        return loadBalancer;
    }

    public synchronized static void init() {
        SpiLoader.load(LoadBalancer.class);
    }

}

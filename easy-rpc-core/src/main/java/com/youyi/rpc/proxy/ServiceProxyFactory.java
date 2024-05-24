package com.youyi.rpc.proxy;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.lb.LoadBalancerKeys;
import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClazz 服务类
     * @param version      服务版本
     * @param <T>          T
     * @return 代理对象 {@link T}
     */
    public static <T> T getProxy(Class<T> serviceClazz, String version) {
        return getProxy(serviceClazz, version, RpcConstants.DEFAULT_SERVICE_GROUP,
                LoadBalancerKeys.ROUND_ROBIN);
    }

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClazz 服务类
     * @param version      服务版本
     * @param group        服务分组
     * @param <T>          T
     * @return 代理对象 {@link T}
     */
    public static <T> T getProxy(Class<T> serviceClazz, String version, String group) {
        return getProxy(serviceClazz, version, group, LoadBalancerKeys.ROUND_ROBIN);
    }


    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClazz 服务类
     * @param version      服务版本
     * @param group        服务分组
     * @param loadBalancer 负载均衡
     * @param <T>          T
     * @return 代理对象 {@link T}
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> serviceClazz, String version, String group,
            String loadBalancer) {
        if (RpcApplication.resolve().isMock()) {
            return getMockProxy(serviceClazz);
        }
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new ServiceProxy(version, group, loadBalancer)
        );
    }

    /**
     * 获取 Mock 代理对象
     *
     * @param serviceClazz 服务类
     * @param <T>          T
     * @return 代理对象 {@link T}
     */
    @SuppressWarnings("unchecked")
    public static <T> T getMockProxy(Class<T> serviceClazz) {
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new MockProxy()
        );
    }
}

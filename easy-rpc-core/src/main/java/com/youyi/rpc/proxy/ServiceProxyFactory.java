package com.youyi.rpc.proxy;

import com.youyi.rpc.RpcApplication;
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
     * @param <T>          T
     * @return 代理对象 {@link T}
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> serviceClazz) {
        if (RpcApplication.resolve().isMock()) {
            return getMockProxy(serviceClazz);
        }
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new ServiceProxy()
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

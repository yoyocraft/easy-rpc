package com.youyi.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ServiceProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> serviceClazz) {
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new ServiceProxy()
        );
    }

}

package com.youyi.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册中心
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private static final Map<String /* 服务名称 */, Class<?> /* 服务实现类 */> REGISTER_SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName service name
     * @param implClass   class
     */
    public static void registry(String serviceName, Class<?> implClass) {
        REGISTER_SERVICE_MAP.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName service name
     * @return class
     */
    public static Class<?> get(String serviceName) {
        return REGISTER_SERVICE_MAP.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName service name
     */
    public static void remove(String serviceName) {
        REGISTER_SERVICE_MAP.remove(serviceName);
    }

}

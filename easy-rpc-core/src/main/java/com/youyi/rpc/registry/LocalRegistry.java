package com.youyi.rpc.registry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 * <p>
 * 作用：根据服务名获取服务类，完成调用
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class LocalRegistry {

    private static final Map<String /* service name */, Class<?> /* service class  */> SERVICE_NAME_CLASS_MAP
            = new ConcurrentHashMap<>(16);

    public static boolean register(String serviceName, Class<?> clazz) {
        return SERVICE_NAME_CLASS_MAP.putIfAbsent(serviceName, clazz) == null;
    }

    public static Optional<Class<?>> get(String serviceName) {
        return Optional.of(SERVICE_NAME_CLASS_MAP.get(serviceName));
    }

    public static boolean remove(String serviceName) {
        return SERVICE_NAME_CLASS_MAP.remove(serviceName) != null;
    }
}

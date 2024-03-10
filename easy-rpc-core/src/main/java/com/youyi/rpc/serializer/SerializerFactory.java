package com.youyi.rpc.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化工厂
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public final class SerializerFactory {

    private static final int INITIAL_SERIALIZER_COUNT = 4;

    private static final Map<String, Serializer> SERIALIZER_MAP = new ConcurrentHashMap<>(
            INITIAL_SERIALIZER_COUNT);

    public static Serializer getSerializer(String key) {
        return SERIALIZER_MAP.computeIfAbsent(key, SerializerFactory::init);
    }

    /**
     * 初始化序列化器
     *
     * @param key 序列化器名
     * @return 序列化器
     */
    private static Serializer init(String key) {
        Serializer serializer;
        switch (key) {
            case SerializerKeys.JDK -> serializer = new JdkSerializer();
            case SerializerKeys.JSON -> serializer = new JsonSerializer();
            case SerializerKeys.KRYO -> serializer = new KryoSerializer();
            case SerializerKeys.HESSIAN -> serializer = new HessianSerializer();
            default -> throw new IllegalArgumentException(
                    "Unsupported serialization protocol: " + key);
        }
        SERIALIZER_MAP.put(key, serializer);
        return serializer;
    }

}

package com.youyi.rpc.serial;

import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.spi.SpiLoader;

/**
 * 序列化工厂
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public final class SerializerFactory {

    /**
     * 获取 Serializer
     *
     * @param key key
     * @return serializer
     */
    public static Serializer getSerializer(String key) {
        Serializer serializer;
        try {
            serializer = SpiLoader.getInstance(Serializer.class, key);
        } catch (NoSuchLoadClassException e) {
            init();
            serializer = SpiLoader.getInstance(Serializer.class, key);
        }
        return serializer;
    }

    public static synchronized void init() {
        SpiLoader.load(Serializer.class);
    }
}

package com.youyi.rpc.registry;

import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.spi.SpiLoader;

/**
 * 注册中心工厂
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public final class RegistryFactory {

    /**
     * 获取注册中心
     *
     * @param key key
     * @return registry
     */
    public static Registry getRegistry(String key) {
        Registry registry;
        try {
            registry = SpiLoader.getInstance(Registry.class, key);
        } catch (NoSuchLoadClassException e) {
            init();
            registry = SpiLoader.getInstance(Registry.class, key);
        }
        return registry;
    }

    public synchronized static void init() {
        SpiLoader.load(Registry.class);
    }
}

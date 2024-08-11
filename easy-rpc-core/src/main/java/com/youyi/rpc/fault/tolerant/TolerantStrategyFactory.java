package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.spi.SpiLoader;

/**
 * 容错策略工厂
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class TolerantStrategyFactory {

    /**
     * 获取容错策略
     *
     * @param key key
     * @return tolerant strategy
     */
    public static TolerantStrategy getTolerantStrategy(String key) {
        TolerantStrategy tolerantStrategy;
        try {
            tolerantStrategy = SpiLoader.getInstance(TolerantStrategy.class, key);
        } catch (NoSuchLoadClassException e) {
            init();
            tolerantStrategy = SpiLoader.getInstance(TolerantStrategy.class, key);
        }
        return tolerantStrategy;
    }

    public static synchronized void init() {
        SpiLoader.load(TolerantStrategy.class);
    }

}

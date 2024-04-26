package com.youyi.rpc.fault.tolerant.mock;

import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.spi.SpiLoader;

/**
 * Mock 容错策略工厂
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MockStrategyFactory {

    /**
     * 获取 mock 策略
     *
     * @param key key
     * @return mock strategy
     */
    public static MockStrategy getMockStrategy(String key) {
        MockStrategy mockStrategy;
        try {
            mockStrategy = SpiLoader.getInstance(MockStrategy.class, key);
        } catch (NoSuchLoadClassException e) {
            init();
            mockStrategy = SpiLoader.getInstance(MockStrategy.class, key);
        }
        return mockStrategy;
    }

    public static synchronized void init() {
        SpiLoader.load(MockStrategy.class);
    }

}

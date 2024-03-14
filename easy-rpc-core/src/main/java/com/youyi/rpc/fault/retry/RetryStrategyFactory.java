package com.youyi.rpc.fault.retry;

import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.spi.SpiLoader;

/**
 * 重试策略工厂
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class RetryStrategyFactory {

    /**
     * 获取重试策略
     *
     * @param key key
     * @return retry strategy
     */
    public static RetryStrategy getRetryStrategy(String key) {
        RetryStrategy retryStrategy;
        try {
            retryStrategy = SpiLoader.getInstance(RetryStrategy.class, key);
        } catch (NoSuchLoadClassException e) {
            init();
            retryStrategy = SpiLoader.getInstance(RetryStrategy.class, key);
        }
        return retryStrategy;
    }

    public synchronized static void init() {
        SpiLoader.load(RetryStrategy.class);
    }

}

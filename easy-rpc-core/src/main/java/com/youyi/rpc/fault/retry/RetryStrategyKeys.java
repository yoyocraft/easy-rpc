package com.youyi.rpc.fault.retry;

/**
 * 重试策略 key
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface RetryStrategyKeys {

    String NO = "no";

    String FIXED_INTERVAL = "fixed_interval";

    String EXPONENTIAL_BACKOFF = "exponential_backoff";
}

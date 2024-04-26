package com.youyi.rpc.fault.retry;

/**
 * 重试策略 key
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class RetryStrategyKeys {

    public static final String NO = "no";

    public static final String FIXED_INTERVAL = "fixed_interval";

    public static final String EXPONENTIAL_BACKOFF = "exponential_backoff";
}

package com.youyi.rpc.fault.tolerant;

/**
 * 容错策略 key
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TolerantStrategyKeys {

    /**
     * 降级处理
     */
    public static final String FAIL_BACK = "fail_back";

    /**
     * 快速失败
     */
    public static final String FAIL_FAST = "fail_fast";

    /**
     * 故障转移
     */
    public static final String FAIL_OVER = "fail_over";

    /**
     * 静默处理
     */
    public static final String FAIL_SAFE = "fail_safe";

}

package com.youyi.rpc.fault.tolerant;

/**
 * 容错策略 key
 *
 * @author <a href="https://github.com/dingxinliang88">codejuzi</a>
 */
public interface TolerantStrategyKeys {

    /**
     * 降级处理
     */
    String FAIL_BACK = "fail_back";

    /**
     * 快速失败
     */
    String FAIL_FAST = "fail_fast";

    /**
     * 故障转移
     */
    String FAIL_OVER = "fail_over";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "fail_safe";

}

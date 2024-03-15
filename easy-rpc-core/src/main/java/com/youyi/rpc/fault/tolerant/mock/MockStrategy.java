package com.youyi.rpc.fault.tolerant.mock;

/**
 * 模拟策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface MockStrategy {

    /**
     * 模拟
     *
     * @return 模拟结果
     */
    Object mock(Object... args);
}

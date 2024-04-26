package com.youyi.rpc.fault.tolerant.mock;

import com.youyi.rpc.spi.SPI;

/**
 * 模拟策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@SPI
public interface MockStrategy {

    /**
     * 模拟
     *
     * @return 模拟结果
     */
    Object mock(Object... args);
}

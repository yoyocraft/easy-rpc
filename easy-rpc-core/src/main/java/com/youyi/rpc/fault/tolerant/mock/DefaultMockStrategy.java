package com.youyi.rpc.fault.tolerant.mock;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认的 mock 策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class DefaultMockStrategy implements MockStrategy {

    @Override
    public Object mock(Object... args) {
        log.info("execute defaultMockStrategy mock, args: {}", args);
        return null;
    }
}

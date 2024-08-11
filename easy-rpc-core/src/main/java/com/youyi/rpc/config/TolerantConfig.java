package com.youyi.rpc.config;

import com.youyi.rpc.fault.retry.RetryStrategyKeys;
import com.youyi.rpc.fault.tolerant.TolerantStrategyKeys;
import com.youyi.rpc.fault.tolerant.mock.MockStrategyKeys;
import lombok.Data;

/**
 * RPC 容错配置
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Data
public class TolerantConfig {

    /**
     * 重试策略
     */
    private String retry = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerant = TolerantStrategyKeys.FAIL_FAST;

    /**
     * 容错策略失败后降级策略，仅限 fail_back
     */
    private String failBackService = MockStrategyKeys.DEFAULT;
}

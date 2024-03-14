package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.model.RpcResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 失败自动降级策略
 *
 * @author <a href="https://github.com/dingxinliang88">codejuzi</a>
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        // TODO 自动降级逻辑
        return null;
    }
}

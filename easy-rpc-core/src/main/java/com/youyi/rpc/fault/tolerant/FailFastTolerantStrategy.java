package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.model.RpcResponse;
import java.util.Map;

/**
 * 快速失败策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class FailFastTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("server error", e);
    }
}

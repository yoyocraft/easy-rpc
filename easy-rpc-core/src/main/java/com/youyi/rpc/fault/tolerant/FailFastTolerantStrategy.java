package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.exception.RpcException;
import com.youyi.rpc.model.RpcResponse;
import java.util.Map;

/**
 * 快速失败策略
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class FailFastTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        throw new RpcException(e.getMessage(), e);
    }
}

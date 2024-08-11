package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.model.RpcResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 静默处理
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        log.error("tolerate exception, ", e);
        return new RpcResponse();
    }
}

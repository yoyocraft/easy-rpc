package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.model.RpcResponse;
import java.util.Map;

/**
 * 容错策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface TolerantStrategy {


    /**
     * 容错
     *
     * @param context 上下文
     * @param e       异常
     * @return RpcResponse
     */
    RpcResponse tolerant(Map<String, Object> context, Exception e) throws Exception;

}

package com.youyi.rpc.fault.retry;

import com.youyi.rpc.model.RpcResponse;
import java.util.concurrent.Callable;

/**
 * 不重试策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class NoRetryStrategy implements RetryStrategy {

    @Override
    public RpcResponse retry(Callable<RpcResponse> task) throws Exception {
        return task.call();
    }
}

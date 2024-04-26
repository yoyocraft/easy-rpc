package com.youyi.rpc.fault.retry;

import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.spi.SPI;
import java.util.concurrent.Callable;

/**
 * 重试策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@SPI
public interface RetryStrategy {


    /**
     * 重试
     *
     * @param task 任务
     * @return 结果
     * @throws Exception 异常
     */
    RpcResponse retry(Callable<RpcResponse> task) throws Exception;

}

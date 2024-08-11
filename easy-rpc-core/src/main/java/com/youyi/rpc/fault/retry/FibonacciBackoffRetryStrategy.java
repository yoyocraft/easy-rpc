package com.youyi.rpc.fault.retry;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.youyi.rpc.model.RpcResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 斐波那契重试策略
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class FibonacciBackoffRetryStrategy implements RetryStrategy {

    private static final long MULTIPLIER = 100L;
    private static final long MAXIMUM_WAIT = 2L;
    private static final int ATTEMPT_NUMBER = 5;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public RpcResponse retry(Callable<RpcResponse> task) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder
                .<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fibonacciWait(MULTIPLIER, MAXIMUM_WAIT,
                        TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(ATTEMPT_NUMBER))
                .withRetryListener(new EasyRpcRetryListener())
                .build();

        return retryer.call(task);
    }
}

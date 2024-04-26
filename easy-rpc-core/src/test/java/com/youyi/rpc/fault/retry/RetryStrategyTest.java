package com.youyi.rpc.fault.retry;


import com.youyi.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
class RetryStrategyTest {

    private final RetryStrategy retryStrategy = new ExponentialBackoffRetryStrategy();

    @Test
    void retry() {

        try {
            RpcResponse rpcResponse = retryStrategy.retry(() -> {
                log.info("test retry!");
                throw new RuntimeException("test for retry");
            });
            log.info("{}", rpcResponse);
        } catch (Exception e) {
            log.error("retry times failed!", e);
        }
    }
}
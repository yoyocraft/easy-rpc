package com.youyi.rpc.fault.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 重试监听器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class EasyRpcRetryListener implements RetryListener {

    @Override
    public <RpcResponse> void onRetry(Attempt<RpcResponse> attempt) {
        log.warn("retry count: {}", attempt.getAttemptNumber());
        log.warn("delay time to the first retry: {}", attempt.getDelaySinceFirstAttempt());
        if (attempt.hasException()) {
            log.error("retry error:", attempt.getExceptionCause());
        }
    }
}

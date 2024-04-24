package com.youyi.rpc.fault.tolerant;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.fault.tolerant.mock.MockStrategy;
import com.youyi.rpc.fault.tolerant.mock.MockStrategyFactory;
import com.youyi.rpc.model.RpcResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 失败自动降级策略
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e) {
        MockStrategy mockStrategy = MockStrategyFactory.getMockStrategy(
                RpcApplication.resolve().getTolerant().getFailBackService());

        log.info("fail back mock strategy:{}", mockStrategy);

        Object mockRes = mockStrategy.mock();
        return RpcResponse.builder()
                .data(mockRes)
                .message("mock ok")
                .build();
    }
}

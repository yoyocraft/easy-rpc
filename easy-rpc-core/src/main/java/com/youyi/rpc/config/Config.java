package com.youyi.rpc.config;

import com.youyi.rpc.fault.retry.RetryStrategyKeys;
import com.youyi.rpc.fault.tolerant.TolerantStrategyKeys;
import com.youyi.rpc.lb.LoadBalancerKeys;
import com.youyi.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class Config {

    private String name = "easy-rpc-framework";
    private String host = "127.0.0.1";
    private int port = 8080;
    private String version = "1.0.0";
    private boolean mock = false;
    private String serializer = SerializerKeys.HESSIAN;
    private RegistryConfig registry = new RegistryConfig();
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    private String retry = RetryStrategyKeys.NO;
    private String tolerant = TolerantStrategyKeys.FAIL_FAST;
}

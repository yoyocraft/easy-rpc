package com.youyi.rpc.lb;

/**
 * 负载均衡器 key 常量
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface LoadBalancerKeys {

    String ROUND_ROBIN = "round_robin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistent_hash";

}

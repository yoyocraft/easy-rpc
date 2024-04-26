package com.youyi.rpc.starter.annotation;

import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.fault.retry.RetryStrategyKeys;
import com.youyi.rpc.fault.tolerant.TolerantStrategyKeys;
import com.youyi.rpc.lb.LoadBalancerKeys;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者注解（用于注入服务）
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {


    /**
     * @return 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * @return 版本
     */
    String serviceVersion() default RpcConstants.DEFAULT_SERVICE_VERSION;

    /**
     * @return 负载均衡器
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * @return 重试机制
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * @return 容错机制
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * @return 是否是模拟调用
     */
    boolean mock() default false;
}

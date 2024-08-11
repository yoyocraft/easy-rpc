package com.youyi.rpc.starter.annotation;

import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.lb.LoadBalancerKeys;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者注解（用于注入服务）
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
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
    String version() default RpcConstants.DEFAULT_SERVICE_VERSION;

    /**
     * @return 服务分组
     */
    String group() default RpcConstants.DEFAULT_SERVICE_GROUP;

    /**
     * @return 负载均衡器
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * @return 是否是模拟调用
     */
    boolean mock() default false;

    /**
     * @return 超时时间
     */
    long timeout() default RpcConstants.DEFAULT_TIMEOUT;
}

package com.youyi.rpc.starter.annotation;

import com.youyi.rpc.constants.RpcConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解（用于注册服务）
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    /**
     * @return 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * @return 版本
     */
    String version() default RpcConstants.DEFAULT_SERVICE_VERSION;

    /**
     * @return 分组
     */
    String group() default RpcConstants.DEFAULT_SERVICE_GROUP;
}

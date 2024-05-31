package com.youyi.rpc.starter.annotation;

import com.youyi.rpc.starter.bootstrap.RpcConsumerBootstrap;
import com.youyi.rpc.starter.bootstrap.RpcInitBootstrap;
import com.youyi.rpc.starter.bootstrap.RpcProviderBootstrap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 开启rpc功能
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * @return 是否需要开启 Server
     */
    boolean needServer() default true;
}

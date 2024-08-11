package com.youyi.rpc.starter.annotation;

import com.youyi.rpc.starter.bootstrap.RpcServiceInjector;
import com.youyi.rpc.starter.bootstrap.RpcFrameworkInitializer;
import com.youyi.rpc.starter.bootstrap.RpcServiceProviderRegistrar;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 开启rpc功能
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcFrameworkInitializer.class, RpcServiceProviderRegistrar.class, RpcServiceInjector.class})
public @interface EnableRpc {

    /**
     * @return 是否需要开启 Server
     */
    boolean needServer() default true;
}

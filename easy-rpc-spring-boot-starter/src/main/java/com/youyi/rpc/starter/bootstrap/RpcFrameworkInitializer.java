package com.youyi.rpc.starter.bootstrap;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.server.RpcServer;
import com.youyi.rpc.server.tcp.VertxTcpServer;
import com.youyi.rpc.starter.annotation.EnableRpc;

import java.util.Map;
import java.util.Objects;

import lombok.NonNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * rpc 初始化引导类
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class RpcFrameworkInitializer implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        // 获取 @EnableRpc 注解的属性值
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableRpc.class.getName());
        boolean needServer = (boolean) Objects.requireNonNull(attributes).get("needServer");

        // RPC 框架初始化
        RpcApplication.init();

        if (needServer) {
            // 全局配置
            final ApplicationConfig applicationConfig = RpcApplication.resolve();
            // 启动服务
            RpcServer rpcServer = new VertxTcpServer();
            rpcServer.doStart(applicationConfig.getPort());
        }
    }
}

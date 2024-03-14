package com.youyi.rpc.starter.bootstrap;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.server.RpcServer;
import com.youyi.rpc.server.tcp.VertxTcpServer;
import com.youyi.rpc.starter.annotation.EnableRpc;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * rpc 初始化引导类
 *
 * @author <a href="https://github.com/dingxinliang88">codejuzi</a>
 */
@Slf4j
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
            @NonNull BeanDefinitionRegistry registry) {
        // 获取 @EnableRpc 注解的属性值
        boolean needServer = (boolean) Objects.requireNonNull(
                        importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()))
                .get("needServer");

        // RPC 框架初始化
        RpcApplication.init();

        // 全局配置
        final Config config = RpcApplication.resolve();

        if (needServer) {
            // 启动 服务器
            RpcServer rpcServer = new VertxTcpServer();
            rpcServer.doStart(config.getPort());
        }
    }
}

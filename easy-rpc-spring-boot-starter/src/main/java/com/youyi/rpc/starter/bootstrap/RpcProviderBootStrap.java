package com.youyi.rpc.starter.bootstrap;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.starter.annotation.RpcService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * RPC 服务提供者启动类
 * <p>
 * 获取到包含 @RpcService 注解的类，通过注解的属性和反射机制，获取到要注册的服务信息，完成服务注册
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class RpcProviderBootStrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {
        Class<?> beanClazz = bean.getClass();
        RpcService rpcService = beanClazz.getAnnotation(RpcService.class);
        // 需要注册服务
        if (rpcService != null) {
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClazz.getInterfaces()[0];
            }

            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 全局配置
            final ApplicationConfig applicationConfig = RpcApplication.resolve();
            // 获取注册中心
            RegistryConfig registryConfig = applicationConfig.getRegistry();
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());

            // 2. 注册服务
            LocalRegistry.registry(serviceName, beanClazz);

            ServiceMetadata serviceMetadata = new ServiceMetadata();
            serviceMetadata.setServiceName(serviceName);
            serviceMetadata.setServiceHost(applicationConfig.getHost());
            serviceMetadata.setServicePort(applicationConfig.getPort());
            serviceMetadata.setServiceVersion(serviceVersion);

            try {
                // 注册到注册中心
                registry.register(serviceMetadata);
            } catch (Exception e) {
                throw new RuntimeException("register service failed", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

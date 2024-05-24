package com.youyi.rpc.bootstrap;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.exception.RpcException;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.model.ServiceRegisterInfo;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.server.RpcServer;
import com.youyi.rpc.server.tcp.VertxTcpServer;
import java.util.List;

/**
 * 服务提供者启动引导类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置、注册中心）
        RpcApplication.init();
        // 全局配置
        final ApplicationConfig applicationConfig = RpcApplication.resolve();
        // 获取注册中心
        RegistryConfig registryConfig = applicationConfig.getRegistry();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getType());

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            Class<?> implClass = serviceRegisterInfo.getImplClass();

            // 本地注册
            LocalRegistry.registry(serviceName, implClass);

            ServiceMetadata serviceMetadata = new ServiceMetadata();
            serviceMetadata.setServiceName(serviceName);
            serviceMetadata.setServiceHost(applicationConfig.getHost());
            serviceMetadata.setServicePort(applicationConfig.getPort());

            try {
                // 注册到注册中心
                registry.register(serviceMetadata);
            } catch (Exception e) {
                throw new RpcException("register service failed", e);
            }
        }

        // 启动 Provider Web 服务
        RpcServer rpcServer = new VertxTcpServer();
        rpcServer.doStart(applicationConfig.getPort());
    }
}

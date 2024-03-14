package com.youyi.rpc.bootstrap;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.config.RegistryConfig;
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
 * @author <a href="https://github.com/dingxinliang88">codejuzi</a>
 */
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置、注册中心）
        RpcApplication.init();
        // 全局配置
        final Config config = RpcApplication.resolve();
        // 获取注册中心
        RegistryConfig registryConfig = config.getRegistry();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            Class<?> implClass = serviceRegisterInfo.getImplClass();

            // 本地注册
            LocalRegistry.registry(serviceName, implClass);

            ServiceMetadata serviceMetadata = new ServiceMetadata();
            serviceMetadata.setServiceName(serviceName);
            serviceMetadata.setServiceHost(config.getHost());
            serviceMetadata.setServicePort(config.getPort());

            try {
                // 注册到注册中心
                registry.register(serviceMetadata);
            } catch (Exception e) {
                throw new RuntimeException("register service failed", e);
            }
        }

        // 启动 Provider Web 服务
        RpcServer rpcServer = new VertxTcpServer();
        rpcServer.doStart(RpcApplication.resolve().getPort());

    }
}

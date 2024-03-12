package com.youyi.example.provider;

import com.youyi.example.common.service.UserService;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.server.RpcServer;
import com.youyi.rpc.server.tcp.VertxTcpServer;

/**
 * 简易服务提供者示例
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ProviderExample {

    public static void main(String[] args) {

        init();

        // 启动 Web 服务
        RpcServer rpcServer = new VertxTcpServer();
        rpcServer.doStart(RpcApplication.resolve().getPort());
    }

    static void init() {
        RpcApplication.init();

        String serviceName = UserService.class.getName();

        // 注册服务
        LocalRegistry.registry(serviceName, UserServiceImpl.class);

        Config config = RpcApplication.resolve();
        RegistryConfig registryConfig = config.getRegistry();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());

        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName(serviceName);
        serviceMetadata.setServiceHost(config.getHost());
        serviceMetadata.setServicePort(config.getPort());

        try {
            registry.register(serviceMetadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

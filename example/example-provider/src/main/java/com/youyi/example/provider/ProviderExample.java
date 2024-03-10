package com.youyi.example.provider;

import com.youyi.example.common.service.UserService;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.server.HttpServer;
import com.youyi.rpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ProviderExample {

    public static void main(String[] args) {

        // 注册服务
        LocalRegistry.registry(UserService.class.getName(), UserServiceImpl.class);

        // 启动 Web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.resolve().getPort());
    }
}

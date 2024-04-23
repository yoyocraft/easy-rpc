package com.youyi.example.provider;

import com.youyi.example.common.service.UserService;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.server.RpcServer;
import com.youyi.rpc.server.VertxRpcServer;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        main0();
    }

    private static void main0() {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        RpcServer rpcServer = new VertxRpcServer();
        rpcServer.doStart(8888);
    }

}

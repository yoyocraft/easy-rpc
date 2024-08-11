package com.youyi.rpc.bootstrap;

import com.youyi.rpc.RpcApplication;

/**
 * 服务消费者启动引导类
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class ConsumerBootstrap {

    public static void init() {
        // RPC 框架初始化
        RpcApplication.init();
    }

}

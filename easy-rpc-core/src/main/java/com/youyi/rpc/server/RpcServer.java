package com.youyi.rpc.server;

/**
 * RPC 服务器接口
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public interface RpcServer {

    /**
     * 启动 web 服务器
     *
     * @param port 端口号
     */
    void doStart(int port);
}

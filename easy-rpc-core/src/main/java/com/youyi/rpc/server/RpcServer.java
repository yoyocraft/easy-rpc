package com.youyi.rpc.server;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface RpcServer {

    /**
     * 启动服务器
     *
     * @param port 端口
     */
    void doStart(int port);

}

package com.youyi.rpc.server;

/**
 * HTTP 服务器接口
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface HttpServer {

    /**
     * 启动 web 服务器
     *
     * @param port 端口号
     */
    void doStart(int port);
}

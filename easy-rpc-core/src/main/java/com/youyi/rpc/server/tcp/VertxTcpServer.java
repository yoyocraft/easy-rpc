package com.youyi.rpc.server.tcp;

import com.youyi.rpc.server.RpcServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx TCP 服务器
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class VertxTcpServer implements RpcServer {

    @Override
    public void doStart(int port) {

        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer netServer = vertx.createNetServer();

        // 处理请求、启动 TCP 服务器并监听指定端口
        netServer
                .connectHandler(new TcpServerHandler())
                .listen(port)
                .onSuccess(event -> log.info("TCP Server started on port {}", port))
                .onFailure(event -> log.error("Failed to start TCP Server on port {}", port));

    }
}

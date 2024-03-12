package com.youyi.rpc.server.http;

import com.youyi.rpc.server.RpcServer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx HTTP 服务器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxHttpServer implements RpcServer {

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        HttpServer httpServer = vertx.createHttpServer();

        // 监听端口并处理请求
        httpServer
                // 绑定自定义处理器
                .requestHandler(new HttpServerHandler())
                .listen(port)
                .onSuccess(server -> log.info("HTTP server started on port {}", port))
                .onFailure(throwable -> log.error("Failed to start HTTP server", throwable));

    }
}

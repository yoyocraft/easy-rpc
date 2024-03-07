package com.youyi.rpc.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx Web 服务器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        // 监听端口并处理请求
        httpServer
                .requestHandler(request -> {
                    // 处理 HTTP 请求
                    log.info("Received request, method: {}, uri: {}", request.method(), request.uri());

                    // 发送 HTTP 响应
                    request.response()
                            .putHeader("content-type", "text/plain")
                            .end("Hello, this is Vert.x HTTP Server");
                })
                .listen(port)
                .onSuccess(server -> log.info("HTTP server started on port {}", port))
                .onFailure(throwable -> log.error("Failed to start HTTP server", throwable));

    }
}

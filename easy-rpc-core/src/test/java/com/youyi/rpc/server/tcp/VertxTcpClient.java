package com.youyi.rpc.server.tcp;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxTcpClient {

    public void start() {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient()
                .connect(8888, "localhost")
                .onSuccess(socket -> {
                    log.info("Connect to TCP server!");

                    // 发送数据
                    socket.write("Hello, TCP server!");

                    // 接收数据
                    socket.handler(buffer -> log.info("Received data from TCP server: {}",
                            buffer.toString()));
                })
                .onFailure(throwable -> log.error("Failed to connect to TCP server: {}",
                        throwable.getMessage()));
    }

}

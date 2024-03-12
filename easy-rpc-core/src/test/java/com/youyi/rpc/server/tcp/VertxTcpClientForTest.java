package com.youyi.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxTcpClientForTest {

    public void start() {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient()
                .connect(8888, "localhost")
                .onSuccess(socket -> {
                    log.info("Connect to TCP server!");

                    // 发送 1000 次请求，模拟粘包、半包问题
                    for (int i = 0; i < 1000; i++) {
                        Buffer buffer = Buffer.buffer();
                        // 发送数据
                        String str = "Hello, TCP server!Hello, TCP server!Hello, TCP server!";

                        buffer.appendInt(0);
                        buffer.appendInt(str.getBytes().length);
                        buffer.appendBytes(str.getBytes());
                        socket.write(buffer);
                    }

                    // 接收数据
                    socket.handler(buffer -> log.info("Received data from TCP server: {}",
                            buffer.toString()));
                })
                .onFailure(throwable -> log.error("Failed to connect to TCP server: {}",
                        throwable.getMessage()));
    }

}

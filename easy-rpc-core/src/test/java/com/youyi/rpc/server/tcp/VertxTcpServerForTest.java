package com.youyi.rpc.server.tcp;

import com.youyi.rpc.server.RpcServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx TCP 服务器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxTcpServerForTest implements RpcServer {

    @Override
    public void doStart(int port) {

        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer netServer = vertx.createNetServer();

        // 处理请求
        netServer.connectHandler(socket -> {

                    // 构造 Parser
                    RecordParser parser = RecordParser.newFixed(8);

                    parser.setOutput(new Handler<>() {
                        int size = -1;
                        // 一次性完整地读取（头 + 体）
                        Buffer resultBuffer = Buffer.buffer();

                        @Override
                        public void handle(Buffer buffer) {
                            if (-1 == size) {
                                // 读取消息体长度
                                size = buffer.getInt(4);
                                parser.fixedSizeMode(size);
                                // 写入头信息
                                resultBuffer.appendBuffer(buffer);
                            } else {
                                // 写入消息体
                                resultBuffer.appendBuffer(buffer);
                                log.info("result: {}", resultBuffer.toString());

                                // 重置一轮
                                parser.fixedSizeMode(8);
                                size = -1;
                                resultBuffer = Buffer.buffer();
                            }
                        }
                    });

                    socket.handler(parser);
                }
        );

        // 启动 TCP 服务器并监听指定端口
        netServer.listen(port)
                .onSuccess(server -> log.info("TCP server started on port {}", port))
                .onFailure(throwable -> log.error("Failed to start TCP server", throwable));
    }
}

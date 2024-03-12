package com.youyi.rpc.server.tcp;

import com.youyi.rpc.server.RpcServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx TCP 服务器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxTcpServer implements RpcServer {

    @Override
    public void doStart(int port) {

        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer netServer = vertx.createNetServer();

        // 处理请求
        netServer.connectHandler(socket ->
                // 处理连接
                socket.handler(buffer -> {
                    // 处理接收到的字节数组
                    byte[] reqData = buffer.getBytes();
                    // 自定义的字节数组处理逻辑，比如解析请求、调用服务、构造响应
                    byte[] respData = handleRequest(reqData);
                    // 发送响应
                    socket.write(Buffer.buffer(respData));
                })
        );

        // 启动 TCP 服务器并监听指定端口
        netServer.listen(port)
                .onSuccess(event -> log.info("TCP Server started on port {}", port))
                .onFailure(event -> log.error("Failed to start TCP Server on port {}", port));

    }

    private byte[] handleRequest(byte[] reqData) {
        // 自定义的处理请求逻辑
        log.info("Received request: {}", new String(reqData));
        // for test
        return "Hello Client!".getBytes();
    }
}

package com.youyi.rpc.server;

import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.serializer.JdkSerializer;
import com.youyi.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class VertxRpcServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(VertxRpcServer.class);

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer
                .requestHandler(new VertxServerHandler())
                .listen(port)
                .onSuccess(server -> logger.info("[VertxRpcServer] start success, port: {}", port))
                .onFailure(e -> logger.error("[VertxRpcServer] start fail, port: {}", port, e));
    }

    static class VertxServerHandler implements Handler<HttpServerRequest> {

        private static final Serializer SERIALIZER = new JdkSerializer();

        private static final Logger logger = LoggerFactory.getLogger(VertxServerHandler.class);

        @Override
        public void handle(HttpServerRequest request) {
            // 记录日志
            logger.info("[VertxServerHandler] receive request: uri={}, method={}", request.uri(),
                    request.method());

            // 异步处理 HTTP 请求
            request.bodyHandler(buffer -> {
                byte[] bytes = buffer.getBytes();
                RpcRequest rpcRequest = null;

                try {
                    rpcRequest = SERIALIZER.deserialize(bytes, RpcRequest.class);
                } catch (IOException e) {
                    logger.error("deserialize error: ", e);
                }

                RpcResponse rpcResponse = new RpcResponse();
                if (Objects.isNull(rpcRequest)) {
                    rpcResponse.setMessage("request is null");
                    doResponse(request, rpcResponse);
                    return;
                }

                try {
                    Class<?> serviceClazz = LocalRegistry.get(
                            rpcRequest.getServiceName()).orElseThrow();
                    Method serviceMethod = serviceClazz.getMethod(rpcRequest.getMethodName(),
                            rpcRequest.getParamsTypes());
                    Object result = serviceMethod.invoke(
                            serviceClazz.getConstructor().newInstance(), rpcRequest.getArgs());

                    rpcResponse.setData(result);
                    rpcResponse.setDataType(serviceMethod.getReturnType());
                    rpcResponse.setMessage("success!");
                } catch (Exception e) {
                    logger.error("provider invoke service error: ", e);
                    rpcResponse.setError(e);
                    rpcResponse.setMessage(e.getMessage());
                }

                doResponse(request, rpcResponse);
            });
        }

        private void doResponse(HttpServerRequest request, RpcResponse rpcResponse) {
            HttpServerResponse response = request.response()
                    .putHeader("Content-Type", "application/json");

            try {
                byte[] bytes = SERIALIZER.serialize(rpcResponse);
                response.end(Buffer.buffer(bytes));
            } catch (IOException e) {
                logger.error("serialize error: ", e);
                response.end(Buffer.buffer(e.getMessage()));
            }
        }
    }
}

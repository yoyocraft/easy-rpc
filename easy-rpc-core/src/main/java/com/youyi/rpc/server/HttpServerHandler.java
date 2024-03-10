package com.youyi.rpc.server;

import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.registry.LocalRegistry;
import com.youyi.rpc.serializer.Serializer;
import com.youyi.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP 请求处理器，基于 Vert.x
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(
                RpcApplication.resolve().getSerializer());

        // 记录日志
        log.info("Received request, method: {}, uri: {}", request.method(), request.uri());

        // 处理 HTTP 请求（异步）
        request.bodyHandler(bodyBuf -> {
            byte[] body = bodyBuf.getBytes();

            // 反序列化
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(body, RpcRequest.class);
            } catch (IOException e) {
                log.error("Deserialize request error, ", e);
            }

            // 构造响应对象
            RpcResponse rpcResponse = new RpcResponse();

            // 请求为 null，返回
            if (Objects.isNull(rpcRequest)) {
                rpcResponse.setMessage("rpc request is null!");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            // 获取要调用的服务实现类，然后通过反射调用，得到结果
            try {
                Class<?> implClazz = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClazz.getMethod(rpcRequest.getMethodName(),
                        rpcRequest.getParameterTypes());
                Object result = method.invoke(implClazz.getDeclaredConstructor().newInstance(),
                        rpcRequest.getParameters());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("execute service successful");
            } catch (Exception e) {
                log.info("execute service error, ", e);
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 响应结果
            doResponse(request, rpcResponse, serializer);
        });

    }

    /**
     * 执行响应
     *
     * @param request     http request
     * @param rpcResponse rpc response
     * @param serializer  actual serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response()
                .putHeader("content-type", "application/json");

        try {
            // 序列化响应结果
            byte[] respRes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(respRes));
        } catch (IOException e) {
            log.error("Serialize response error, ", e);
            response.end(Buffer.buffer());
        }

    }
}

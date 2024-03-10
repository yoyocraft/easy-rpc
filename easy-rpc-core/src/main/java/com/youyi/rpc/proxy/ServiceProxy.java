package com.youyi.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.serializer.Serializer;
import com.youyi.rpc.serializer.SerializerFactory;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务代理，基于 JDK 动态代理
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(
                RpcApplication.resolve().getSerializer());

        log.info("service proxy use serializer: {}", serializer);

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        try {
            // 序列化
            byte[] reqBytes = serializer.serialize(rpcRequest);
            // 发送请求
            // TODO 此处地址为硬编码，需要使用注册中心和服务发现机制解决
            Config config = RpcApplication.resolve();
            String url = "http://" + config.getHost() + ":" + config.getPort();
            try (HttpResponse httpResponse = HttpRequest.post(url)
                    .body(reqBytes).execute()) {
                byte[] respBytes = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(respBytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            log.info("RPC request error, ", e);
        }

        return null;
    }
}

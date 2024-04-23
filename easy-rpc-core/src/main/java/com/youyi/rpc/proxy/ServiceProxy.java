package com.youyi.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.serializer.JdkSerializer;
import com.youyi.rpc.serializer.Serializer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramsTypes(method.getParameterTypes())
                .args(args)
                .build();

        byte[] bytes = serializer.serialize(rpcRequest);

        try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8888")
                .body(bytes).execute()) {
            byte[] resultBytes = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            // check error
            if (rpcResponse.getError() != null) {
                throw rpcResponse.getError();
            }
            return rpcResponse.getData();
        }
    }
}

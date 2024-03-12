package com.youyi.rpc.server.tcp;

import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.protocol.ProtocolMessage;
import com.youyi.rpc.protocol.codec.ProtocolMessageDecoder;
import com.youyi.rpc.protocol.codec.ProtocolMessageEncoder;
import com.youyi.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import java.io.IOException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * TCP Server 请求处理器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    @SuppressWarnings("unchecked")
    @Override
    public void handle(NetSocket socket) {

        // 处理连接
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接收请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;

            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder
                        .decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("decode request error", e);
            }

            RpcRequest rpcRequest = protocolMessage.getBody();

            // 处理请求，构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();

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

            // 发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessage.MessageType.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> respProtocolMessage = new ProtocolMessage<>(header,
                    rpcResponse);

            try {
                Buffer encodeBuffer = ProtocolMessageEncoder.encode(respProtocolMessage);
                socket.write(encodeBuffer);
            } catch (IOException e) {
                throw new RuntimeException("encode response error, ", e);
            }

        });

        // 调用装饰之后的 handler
        socket.handler(tcpBufferHandlerWrapper);

    }
}

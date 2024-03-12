package com.youyi.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.constants.RpcConstant;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.protocol.ProtocolConstant;
import com.youyi.rpc.protocol.ProtocolMessage;
import com.youyi.rpc.protocol.codec.ProtocolMessageDecoder;
import com.youyi.rpc.protocol.codec.ProtocolMessageEncoder;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.serializer.Serializer;
import com.youyi.rpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务代理，基于 JDK 动态代理
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Config config = RpcApplication.resolve();
        final Serializer serializer = SerializerFactory.getSerializer(
                config.getSerializer());

        log.debug("service proxy use serializer: {}", serializer);

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        Registry registry = RegistryFactory.getRegistry(
                config.getRegistry().getRegistry());
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName(serviceName);
        serviceMetadata.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetadata> serviceMetadataList = registry.discovery(
                serviceMetadata.getServiceKey());
        if (CollUtil.isEmpty(serviceMetadataList)) {
            throw new RuntimeException("there are no registry!");
        }
        // TODO 暂时先取第一个作为服务地址
        ServiceMetadata selectedService = serviceMetadataList.get(0);

        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();

        CompletableFuture<RpcResponse> respFuture = new CompletableFuture<>();

        netClient.connect(selectedService.getServicePort(), selectedService.getServiceHost())
                .onSuccess(socket -> {
                    log.info("connect to server success!");
                    // 构造信息
                    ProtocolMessage<RpcRequest> protocolMessage
                            = getRpcRequestProtocolMessage(rpcRequest);

                    // 编码信息，发送数据
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("encode error, ", e);
                    }

                    // 接收响应
                    socket.handler(buffer -> {
                        // 解码响应
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage
                                    = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder
                                    .decode(buffer);
                            respFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("decode error, ", e);
                        }

                    });
                })
                .onFailure(throwable -> log.info("RPC request error, ", throwable));
        RpcResponse rpcResponse = respFuture.get();
        netClient.close();
        return rpcResponse.getData();
    }

    private static ProtocolMessage<RpcRequest> getRpcRequestProtocolMessage(RpcRequest rpcRequest) {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessage.MessageSerializer.resolve(
                RpcApplication.resolve()
                        .getSerializer()).getKey());
        header.setType((byte) ProtocolMessage.MessageType.REQUEST.getKey());
        header.setReqId(IdUtil.getSnowflakeNextId());
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);
        return protocolMessage;
    }
}

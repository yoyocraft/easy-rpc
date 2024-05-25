package com.youyi.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.exception.RpcException;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.protocol.ProtocolConstants;
import com.youyi.rpc.protocol.ProtocolMessage;
import com.youyi.rpc.protocol.codec.ProtocolMessageDecoder;
import com.youyi.rpc.protocol.codec.ProtocolMessageEncoder;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx TCP 客户端
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class VertxTcpClient {

    // private static final NetClient netClient = Vertx.vertx().createNetClient();

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetadata metadata)
            throws ExecutionException, InterruptedException, TimeoutException {
        return doRequest(rpcRequest, metadata, RpcConstants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetadata metadata,
            long timeout) throws ExecutionException, InterruptedException, TimeoutException {
        return doRequest(rpcRequest, metadata, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送 RPC 请求
     *
     * @param rpcRequest rpc 请求
     * @param metadata   服务元数据
     * @param timeout    超时时间
     * @return RpcResponse
     */
    @SuppressWarnings("unchecked")
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetadata metadata,
            long timeout, TimeUnit timeUnit)
            throws ExecutionException, InterruptedException, TimeoutException {
        NetClient netClient = Vertx.vertx().createNetClient();

        CompletableFuture<RpcResponse> respFuture = new CompletableFuture<>();

        netClient.connect(metadata.getServicePort(), metadata.getServiceHost())
                .onSuccess(socket -> {
                    log.info("connect to server success!");
                    // 构造信息
                    ProtocolMessage<RpcRequest> protocolMessage
                            = buildRpcRequestProtocolMessage(rpcRequest);

                    try {
                        // 编码信息
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        // 发送请求
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RpcException("encode error, ", e);
                    }

                    // 接收响应
                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                // 解码响应
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage
                                            = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder
                                            .decode(buffer);
                                    respFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RpcException("decode error, ", e);
                                }
                            });
                    // 装饰器，解决粘包
                    socket.handler(tcpBufferHandlerWrapper);
                })
                .onFailure(throwable -> {
                    log.info("RPC request error, ", throwable);
                    respFuture.completeExceptionally(throwable);
                });
        // 设置超时时间
        respFuture.orTimeout(timeout, timeUnit)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        log.error("RPC request timeout after {} ms", timeout);
                    } else {
                        log.error("RPC request error: ", throwable);
                    }
                    return null;
                });
        try {
            // 异步处理，阻塞
            return respFuture.get(timeout, TimeUnit.MILLISECONDS);
        } finally {
            // 关闭连接
            netClient.close();
        }

    }

    private static ProtocolMessage<RpcRequest> buildRpcRequestProtocolMessage(
            RpcRequest rpcRequest) {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstants.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstants.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessage.MessageSerializer
                .resolve(RpcApplication.resolve().getSerializer()).getKey());
        header.setType((byte) ProtocolMessage.MessageType.REQUEST.getKey());
        header.setReqId(IdUtil.getSnowflakeNextId());
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);
        return protocolMessage;
    }

}

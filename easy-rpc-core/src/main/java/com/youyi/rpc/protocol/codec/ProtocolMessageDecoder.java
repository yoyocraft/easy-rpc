package com.youyi.rpc.protocol.codec;

import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.protocol.ProtocolConstants;
import com.youyi.rpc.protocol.ProtocolMessage;
import com.youyi.rpc.serial.Serializer;
import com.youyi.rpc.serial.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import java.io.IOException;

/**
 * 协议消息解码器
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class ProtocolMessageDecoder {


    /**
     * 解码
     *
     * @param buffer buffer
     * @return ProtocolMessage
     * @throws IOException IO 异常
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 读取 buffer，解析协议头
        ProtocolMessage.Header header = resolveHeader(buffer);

        // 解决粘包问题，只读取指定长度的数据
        byte[] bodyBytes = buffer.getBytes(ProtocolConstants.MESSAGE_HEADER_LENGTH,
                ProtocolConstants.MESSAGE_HEADER_LENGTH + header.getBodyLength());

        // 解析消息体
        ProtocolMessage.MessageSerializer messageSerializer = ProtocolMessage.MessageSerializer.resolve(
                header.getSerializer());
        Serializer serializer = SerializerFactory.getSerializer(messageSerializer.getValue());
        ProtocolMessage.MessageType messageType = ProtocolMessage.MessageType.resolve(
                header.getType());

        switch (messageType) {
            case REQUEST -> {
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, rpcRequest);
            }
            case RESPONSE -> {
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, rpcResponse);
            }
            case HEARTBEAT, OTHER -> // TODO
                    throw new UnsupportedOperationException(
                            "Unsupported message type: " + messageType);
            default -> throw new IllegalStateException("Unexpected value: " + messageType);
        }
    }

    private static ProtocolMessage.Header resolveHeader(Buffer buffer) {
        ProtocolMessage.Header header = new ProtocolMessage.Header();

        byte magic = buffer.getByte(ProtocolConstants.MAGIC_POS); // 1 byte
        if (magic != ProtocolConstants.PROTOCOL_MAGIC) {
            throw new IllegalArgumentException("Invalid magic number: " + magic);
        }

        header.setMagic(magic); // 1 byte
        header.setVersion(buffer.getByte(ProtocolConstants.VERSION_POS)); // 1 byte
        header.setSerializer(buffer.getByte(ProtocolConstants.SERIALIZER_POS)); // 1 byte
        header.setType(buffer.getByte(ProtocolConstants.TYPE_POS)); // 1 byte
        header.setStatus(buffer.getByte(ProtocolConstants.STATUS_POS)); // 1 byte
        header.setReqId(buffer.getLong(ProtocolConstants.REQ_ID_POS)); // 8 byte
        header.setBodyLength(buffer.getInt(ProtocolConstants.BODY_LEN_POS)); // 4 byte

        return header;
    }


}

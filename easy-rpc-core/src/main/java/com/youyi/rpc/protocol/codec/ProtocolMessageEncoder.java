package com.youyi.rpc.protocol.codec;

import com.youyi.rpc.protocol.ProtocolMessage;
import com.youyi.rpc.serial.Serializer;
import com.youyi.rpc.serial.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import java.io.IOException;

/**
 * 协议消息编码器
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param message 消息
     * @return 编码后的buffer
     * @throws IOException IO异常
     */
    public static Buffer encode(ProtocolMessage<?> message) throws IOException {
        if (isNull(message)) {
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = message.getHeader();
        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        appendHeaderWithoutLength(buffer, header);

        // 获取序列化器
        ProtocolMessage.MessageSerializer messageSerializer = ProtocolMessage.MessageSerializer
                .resolve(header.getSerializer());

        Serializer serializer = SerializerFactory.getSerializer(messageSerializer.getValue());
        byte[] bodyBytes = serializer.serialize(message.getBody());

        // append body length and body
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);

        return buffer;
    }

    private static boolean isNull(ProtocolMessage<?> message) {
        return message == null || message.getHeader() == null;
    }

    private static void appendHeaderWithoutLength(Buffer buffer, ProtocolMessage.Header header) {
        // | magic | version | serializer | type | status |
        // |                               reqId                               |
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getReqId());
    }
}

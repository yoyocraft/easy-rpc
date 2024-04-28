package com.youyi.rpc.protocol;


import cn.hutool.core.util.IdUtil;
import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.protocol.codec.ProtocolMessageDecoder;
import com.youyi.rpc.protocol.codec.ProtocolMessageEncoder;
import io.vertx.core.buffer.Buffer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
class ProtocolMessageTest {

    @Test
    public void testEncodeAndDecode() throws IOException {

        // 构造消息
        ProtocolMessage<RpcRequest> message = new ProtocolMessage<>();
        ProtocolMessage.Header header = getHeader();

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("test-service");
        rpcRequest.setMethodName("test-method");
        rpcRequest.setServiceVersion(RpcConstants.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setParameters(new Object[]{"test-args"});

        message.setHeader(header);
        message.setBody(rpcRequest);

        // 编码消息
        Buffer buffer = ProtocolMessageEncoder.encode(message);

        // 解码消息
        ProtocolMessage<?> protocolMessage = ProtocolMessageDecoder.decode(buffer);
        log.info("after decode, message: {}", protocolMessage);

        Assertions.assertNotNull(protocolMessage);

    }

    private static ProtocolMessage.Header getHeader() {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstants.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstants.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessage.MessageSerializer.JDK.getKey());
        header.setType((byte) ProtocolMessage.MessageType.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessage.MessageStatus.OK.getCode());
        header.setReqId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        return header;
    }


}
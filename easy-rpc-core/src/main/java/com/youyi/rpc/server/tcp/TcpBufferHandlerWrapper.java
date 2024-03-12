package com.youyi.rpc.server.tcp;

import com.youyi.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 装饰器模式，使用 RecordParser 对原有的 Buffer 处理能力进行增强，解决粘包半包问题
 * <p>
 * 策略：读两次，第一次读取长度，第二次读取内容
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        log.info("init TcpBufferHandlerWrapper");
        recordParser = initRecordParser(bufferHandler);
    }


    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 Parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<>() {
            // 初始化
            int size = -1;
            // 一次性完整地读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (-1 == size) {
                    // 读取消息体长度
                    size = buffer.getInt(ProtocolConstant.BODY_LEN_POS);
                    parser.fixedSizeMode(size);
                    // 写入头信息
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 写入消息体
                    resultBuffer.appendBuffer(buffer);
                    bufferHandler.handle(resultBuffer);

                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}

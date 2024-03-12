package com.youyi.rpc.protocol;

/**
 * 协议常量
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface ProtocolConstant {

    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x01;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x01;

}

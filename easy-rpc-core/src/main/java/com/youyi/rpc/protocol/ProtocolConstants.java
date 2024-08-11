package com.youyi.rpc.protocol;

/**
 * 协议常量
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class ProtocolConstants {

    /**
     * 消息头长度
     */
    public static final int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    public static final byte PROTOCOL_MAGIC = 0x01;

    /**
     * 协议版本号
     */
    public static final byte PROTOCOL_VERSION = 0x01;

    // ---------------------- index ------------------------

    public static final int MAGIC_POS = 0;
    public static final int VERSION_POS = 1;
    public static final int SERIALIZER_POS = 2;
    public static final int TYPE_POS = 3;
    public static final int STATUS_POS = 4;
    public static final int REQ_ID_POS = 5;
    public static final int BODY_LEN_POS = 13;

    // ------------------------------------------------------

}

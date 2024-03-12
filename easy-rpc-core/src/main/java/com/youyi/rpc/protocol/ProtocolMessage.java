package com.youyi.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议信息结构
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头 (17byte)
     */
    private Header header;

    /**
     * 消息体（请求 / 响应对象）
     */
    private T body;


    /**
     * 消息协议头
     */
    @Data
    public static class Header {

        /**
         * 魔数 (8bit)，保证安全性
         */
        private byte magic;

        /**
         * 版本号 (8bit)
         */
        private byte version;

        /**
         * 序列化器 (8bit)，类似 Content-Type
         */
        private byte serializer;

        /**
         * 消息类型 (8bit)，请求 / 响应 / 心跳 / ...
         */
        private byte type;

        /**
         * 状态 (8bit)，如果是响应，记录响应的结果（类似 HTTP 200状态码）
         */
        private byte status;

        /**
         * 请求 ID (64bit)，唯一标识某个请求
         */
        private long reqId;

        /**
         * 消息体长度 (32bit)
         */
        private int bodyLength;
    }

}

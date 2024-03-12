package com.youyi.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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

    /**
     * 协议消息类型枚举
     *
     * @author <a href="https://github.com/dingxinliang88">youyi</a>
     */
    @Getter
    public enum MessageType {

        REQUEST(0),
        RESPONSE(1),
        HEARTBEAT(2),
        OTHER(100),
        ;

        private final int key;

        MessageType(int key) {
            this.key = key;
        }

        public static MessageType resolve(int key) {

            for (MessageType type : MessageType.values()) {
                if (type.key == key) {
                    return type;
                }
            }

            throw new IllegalArgumentException("unknown protocol message type: " + key);
        }
    }

    /**
     * 协议消息序列化器枚举
     *
     * @author <a href="https://github.com/dingxinliang88">youyi</a>
     */
    @Getter
    public enum MessageSerializer {

        JDK(0, "jdk"),
        JSON(1, "json"),
        KRYO(2, "kryo"),
        HESSIAN(3, "hessian"),
        ;

        private final int key;
        private final String value;

        MessageSerializer(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static List<String> getValues() {
            return Arrays.stream(values()).map(MessageSerializer::getValue).collect(
                    Collectors.toList());
        }

        public static MessageSerializer resolve(int key) {

            for (MessageSerializer serializer : values()) {
                if (serializer.key == key) {
                    return serializer;
                }
            }

            throw new IllegalArgumentException("unknown protocol message serializer key: " + key);
        }

        public static MessageSerializer resolve(String value) {

            if (ObjectUtil.isEmpty(value)) {
                return null;
            }

            for (MessageSerializer serializer : MessageSerializer.values()) {
                if (serializer.value.equals(value)) {
                    return serializer;
                }
            }

            throw new IllegalArgumentException(
                    "unknown protocol message serializer value: " + value);
        }

    }

    /**
     * 协议消息状态枚举
     *
     * @author <a href="https://github.com/dingxinliang88">youyi</a>
     */
    @Getter
    public enum MessageStatus {

        OK("ok", 20),
        BAD_REQUEST("badRequest", 40),
        BAD_RESPONSE("badResponse", 50),
        ;

        private final String desc;
        private final int val;

        MessageStatus(String desc, int val) {
            this.desc = desc;
            this.val = val;
        }

        public static MessageStatus resolve(int val) {

            for (MessageStatus status : values()) {
                if (status.val == val) {
                    return status;
                }
            }

            throw new IllegalArgumentException("unknown protocol message status: " + val);
        }
    }

}

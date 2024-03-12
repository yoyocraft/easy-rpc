package com.youyi.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息类型枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum ProtocolMessageType {

    REQUEST(0),
    RESPONSE(1),
    HEARTBEAT(2),
    DISCONNECT(3),
    OTHER(100),
    ;

    private final int key;

    ProtocolMessageType(int key) {
        this.key = key;
    }

    public static ProtocolMessageType resolve(int key) {

        for (ProtocolMessageType type : ProtocolMessageType.values()) {
            if (type.key == key) {
                return type;
            }
        }

        throw new IllegalArgumentException("unknown protocol message type: " + key);
    }
}

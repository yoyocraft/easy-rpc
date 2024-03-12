package com.youyi.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息状态枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum ProtocolMessageStatus {

    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50),
    ;

    private final String desc;
    private final int val;

    ProtocolMessageStatus(String desc, int val) {
        this.desc = desc;
        this.val = val;
    }

    public static ProtocolMessageStatus resolve(int val) {

        for (ProtocolMessageStatus status : values()) {
            if (status.val == val) {
                return status;
            }
        }

        throw new IllegalArgumentException("unknown protocol message status: " + val);
    }
}

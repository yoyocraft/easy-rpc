package com.youyi.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * 协议消息序列化器枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum ProtocolMessageSerializer {

    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian"),
    ;

    private final int key;
    private final String value;

    ProtocolMessageSerializer(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(ProtocolMessageSerializer::getValue).collect(Collectors.toList());
    }

    public static ProtocolMessageSerializer resolve(int key) {

        for (ProtocolMessageSerializer serializer : values()) {
            if (serializer.key == key) {
                return serializer;
            }
        }

        throw new IllegalArgumentException("unknown protocol message serializer key: " + key);
    }

    public static ProtocolMessageSerializer resolve(String value) {

        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        for (ProtocolMessageSerializer serializer : values()) {
            if (serializer.value.equals(value)) {
                return serializer;
            }
        }

        throw new IllegalArgumentException("unknown protocol message serializer value: " + value);
    }

}

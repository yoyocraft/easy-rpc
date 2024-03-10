package com.youyi.rpc.serializer;

import lombok.Getter;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum SerializerType {

    JDK("jdk"),
    KRYO("kryo"),
    ;

    final String type;


    SerializerType(String type) {
        this.type = type;
    }

    public static SerializerType resolve(String type) {

        for (SerializerType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }

        throw new IllegalArgumentException(type + " is not support!");
    }
}

package com.youyi.rpc.serializer;

import com.youyi.rpc.RpcApplication;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class SerializerFactory {

    public static Serializer getSerializer() {
        String type = RpcApplication.resolve().getSerializer();
        SerializerType serializerType = SerializerType.resolve(type);
        Serializer serializer = new KryoSerializer();
        switch (serializerType) {
            case JDK -> serializer = new JdkSerializer();
            case KRYO -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return serializer;
    }

}

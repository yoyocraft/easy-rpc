package com.youyi.rpc.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * JDK 序列化器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class JdkSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
            return clazz.cast(ois.readObject());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

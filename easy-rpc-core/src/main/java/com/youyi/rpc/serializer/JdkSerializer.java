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
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
        } catch (IOException ignored) {
        }

        return bos.toByteArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);

        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

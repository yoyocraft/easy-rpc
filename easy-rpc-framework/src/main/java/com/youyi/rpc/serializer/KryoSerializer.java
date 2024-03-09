package com.youyi.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo 序列化
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class KryoSerializer implements Serializer {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(Kryo::new);

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        Kryo kryo = kryoThreadLocal.get();
        kryo.register(obj.getClass());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Output output = new Output(bos)) {
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        Kryo kryo = kryoThreadLocal.get();
        kryo.register(clazz);
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (Input input = new Input(bis)) {
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

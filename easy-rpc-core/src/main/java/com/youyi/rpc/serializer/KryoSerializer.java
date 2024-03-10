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

    /**
     * Kryo 实例线程不安全，使用 ThreadLocal 确保线程安全
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 设置动态序列化和反序列化类，不提前注册所有类
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        KRYO_THREAD_LOCAL.get().writeObject(output, obj);
        output.close();
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        Input input = new Input(bis);
        T obj = KRYO_THREAD_LOCAL.get().readObject(input, clazz);
        input.close();
        return obj;
    }
}

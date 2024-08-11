package com.youyi.rpc.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo 序列化
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class KryoSerializer implements Serializer {

    /**
     * Kryo 实例线程不安全，使用 ThreadLocal 确保线程安全
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 设置动态序列化和反序列化类，不提前注册所有类
        // kryo.setRegistrationRequired(false);
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Output output = new Output(bos)) {
            kryoThreadLocal.get().writeObject(output, obj);
            return bos.toByteArray();
        } finally {
            kryoThreadLocal.remove();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
                Input input = new Input(bis)) {
            Object obj = kryoThreadLocal.get().readObject(input, clazz);
            return clazz.cast(obj);
        } finally {
            kryoThreadLocal.remove();
        }
    }
}

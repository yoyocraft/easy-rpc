package com.youyi.rpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import java.io.IOException;

/**
 * JSON 序列化器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class JsonSerializer implements Serializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(data, clazz);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, clazz);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }


    /**
     * 由于 Object 的原始对象被擦除，导致反序列化时会被作为 {@link java.util.LinkedHashMap}, 无法转换成原始对象，此处需要做特殊处理
     *
     * @param request rpc request
     * @param type    type
     * @param <T>     {@link  T}
     * @return {@link  T}
     * @throws IOException IOException
     */
    private <T> T handleRequest(RpcRequest request, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        // 循环处理每个参数的类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            // 如果类型不同，重新处理类型
            if (!clazz.isAssignableFrom(parameters[i].getClass())) {
                byte[] paramBytes = OBJECT_MAPPER.writeValueAsBytes(parameters[i]);
                parameters[i] = OBJECT_MAPPER.readValue(paramBytes, clazz);
            }
        }
        return type.cast(request);
    }

    /**
     * 由于 Object 的原始对象被擦除，导致反序列化时会被作为 {@link java.util.LinkedHashMap}, 无法转换成原始对象，此处需要做特殊处理
     *
     * @param response rpc response
     * @param type     type
     * @param <T>      {@link  T}
     * @return {@link  T}
     * @throws IOException IOException
     */
    private <T> T handleResponse(RpcResponse response, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(response.getData());
        response.setData(OBJECT_MAPPER.readValue(dataBytes, response.getDataType()));
        return type.cast(response);
    }
}

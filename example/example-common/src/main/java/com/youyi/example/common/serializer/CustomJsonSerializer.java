package com.youyi.example.common.serializer;

import cn.hutool.json.JSONUtil;
import com.youyi.rpc.serial.Serializer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class CustomJsonSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return JSONUtil.toJsonStr(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return JSONUtil.toBean(new String(data), clazz);
    }
}

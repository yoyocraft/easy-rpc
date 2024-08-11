package com.youyi.rpc.serial;

import com.youyi.rpc.spi.SPI;
import java.io.IOException;

/**
 * 序列化器接口
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj target object
     * @return byte arr
     * @throws IOException io exception
     */
    byte[] serialize(Object obj) throws IOException;

    /**
     * 反序列化
     *
     * @param data  byte arr
     * @param clazz class type
     * @param <T>   T
     * @return target object
     * @throws IOException io exception
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}

package com.youyi.rpc.spi;


import com.youyi.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
class SpiLoaderTest {

    @Test
    void load() {
        SpiLoader.loadAll();
        String key = "custom_jdk";
        Serializer serializer = SpiLoader.getInstance(Serializer.class, key);
        log.info("{}: {}", key, serializer);
    }
}
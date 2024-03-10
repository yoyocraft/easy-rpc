package com.youyi.rpc.utils;


import com.youyi.rpc.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ConfigUtilTest {

    @Test
    void load() {
        Config config = ConfigUtil.load(Config.class, "rpc", "test");
        log.info("test config: {}", config);
    }
}
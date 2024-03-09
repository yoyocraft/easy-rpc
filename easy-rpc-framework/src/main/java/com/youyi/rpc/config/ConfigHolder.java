package com.youyi.rpc.config;

import com.youyi.rpc.utils.ConfigUtil;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC Config Holder
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ConfigHolder {

    private static final Config DEFAULT_CONFIG = new Config();

    private static volatile Config config;

    /**
     * RPC Config 初始化
     *
     * @param conf 配置
     */
    public static void init(Config conf) {
        config = conf;
        log.info("rpc init, config: {}", conf);
    }

    /**
     * RPC Config 初始化
     */
    public static void init() {
        Config conf;

        try {
            conf = ConfigUtil.load(Config.class, "rpc");
        } catch (Exception e) {
            log.error("load rpc properties error, use default config, ", e);
            conf = DEFAULT_CONFIG;
        }
        init(conf);
    }

    /**
     * 获取 RPC 配置
     */
    public static Config resolve() {
        if (Objects.isNull(config)) {
            synchronized (ConfigHolder.class) {
                if (Objects.isNull(config)) {
                    init();
                }
            }
        }
        return config;
    }

}

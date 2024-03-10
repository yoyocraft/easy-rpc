package com.youyi.rpc;

import cn.hutool.core.io.resource.NoResourceException;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.constants.RpcConstant;
import com.youyi.rpc.utils.ConfigUtil;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC APP
 * <p>
 * - Config Holder
 * <p>
 * - APP
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class RpcApplication {

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
            conf = ConfigUtil.load(Config.class, RpcConstant.RPC_CONFIG_PREFIX);
        } catch (NoResourceException e) {
            log.error("load rpc properties error, use default config");
            conf = DEFAULT_CONFIG;
        }
        init(conf);
    }

    /**
     * 获取 RPC 配置
     */
    public static Config resolve() {
        if (Objects.isNull(config)) {
            synchronized (RpcApplication.class) {
                if (Objects.isNull(config)) {
                    init();
                }
            }
        }
        return config;
    }

}

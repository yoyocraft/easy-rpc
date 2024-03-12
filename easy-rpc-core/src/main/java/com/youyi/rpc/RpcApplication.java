package com.youyi.rpc;

import cn.hutool.core.io.resource.NoResourceException;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.constants.RpcConstant;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.utils.ConfigUtil;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC APP
 * <p>
 * <ul>Config Holder</ul>
 * <ul>APP</ul>
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
        // 注册中心初始化
        RegistryConfig registryConfig = conf.getRegistry();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        // 创建并注册 Shutdown Hook, JVM 退出时执行操作
        Runtime.getRuntime()
                .addShutdownHook(new Thread(registry::destroy, "registry-destroy-hook"));
    }

    /**
     * RPC Config 初始化
     */
    public static void init() {
        Config conf;

        try {
            conf = ConfigUtil.load(Config.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
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

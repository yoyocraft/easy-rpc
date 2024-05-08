package com.youyi.rpc;

import cn.hutool.core.io.resource.NoResourceException;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.util.ConfigUtil;
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

    private static final ApplicationConfig DEFAULT_APPLICATION_CONFIG = new ApplicationConfig();

    private static volatile ApplicationConfig applicationConfig;

    /**
     * RPC Config 初始化
     */
    public static void init() {
        ApplicationConfig conf;

        try {
            conf = ConfigUtil.load(ApplicationConfig.class, RpcConstants.DEFAULT_CONFIG_PREFIX);
        } catch (NoResourceException e) {
            log.error("load rpc properties error, use default config");
            conf = DEFAULT_APPLICATION_CONFIG;
        }
        init(conf);
    }

    /**
     * RPC Config 初始化
     *
     * @param conf 配置
     */
    public static void init(ApplicationConfig conf) {
        applicationConfig = conf;
        log.debug("rpc init, config: {}", conf);

        // 注册中心初始化
        RegistryConfig registryConfig = applicationConfig.getRegistry();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getType());
        log.debug("registry init, config = {}", registryConfig);
        registry.init(registryConfig);

        registerJvmHook(registry);
    }

    /**
     * 创建并注册 Shutdown Hook, JVM 退出时执行操作
     */
    private static void registerJvmHook(Registry registry) {
        Runtime.getRuntime()
                .addShutdownHook(new Thread(registry::destroy, "registry-destroy-hook"));
    }

    /**
     * 获取 RPC 配置
     */
    public static ApplicationConfig resolve() {
        if (Objects.isNull(applicationConfig)) {
            synchronized (RpcApplication.class) {
                if (Objects.isNull(applicationConfig)) {
                    init();
                }
            }
        }
        return applicationConfig;
    }

}

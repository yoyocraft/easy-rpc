package com.youyi.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ConfigUtil {

    /**
     * 加载配置 application-{env}.properties
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param <T>    T
     * @return props
     */
    public static <T> T load(Class<T> clazz, String prefix) {
        return load(clazz, prefix, "");
    }


    /**
     * 加载配置 application-{env}.properties
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param env    environment
     * @param <T>    T
     * @return props
     */
    public static <T> T load(Class<T> clazz, String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }

}

package com.youyi.rpc.utils;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置工具类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ConfigUtil {

    /**
     * 加载配置
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
     * 加载配置
     * <p>
     * 优先加载 conf/conf.properties, 找不到再加载 conf.properties
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param env    environment
     * @param <T>    T
     * @return props
     */
    public static <T> T load(Class<T> clazz, String prefix, String env) {
        T props;
        try {
            props = loadProperties(clazz, "conf/conf", prefix, env);
        } catch (NoResourceException e) {
            log.warn("Not exists conf file in [conf/], will load file from classpath");
            props = loadProperties(clazz, "conf", prefix, env);
        }
        return props;
    }

    /**
     * 加载配置 application-{env}.properties
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param <T>    T
     * @return props
     */
    public static <T> T loadProperties(Class<T> clazz, String base, String prefix, String env) {
        return load(clazz, base, prefix, env, "properties");
    }


    /**
     * 加载配置
     *
     * @param clazz  clazz
     * @param prefix properties common prefix
     * @param env    environment
     * @param ext    file extension name
     * @param <T>    T
     * @return props
     */
    public static <T> T load(Class<T> clazz, String base, String prefix, String env, String ext) {
        StringBuilder configFileBuilder = new StringBuilder(base);
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".").append(ext);
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }
}

package com.youyi.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.youyi.rpc.exception.NoSuchKeyException;
import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.serializer.Serializer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * SPI 加载器
 * <p>
 * 支持键值对映射
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class SpiLoader {

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * SPI 扫描路径，顺序不可以更改，因为 custom > system，后续的会覆盖前面的
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * SPI 文件 Key - Value 分隔符
     */
    private static final String SPI_SEPARATOR = "=";

    /**
     * 存储已经加载的类
     */
    private static final Map<String /* 接口名 */, Map<String /* 实现类 key */, Class<?> /* 实现类 */>> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存，单例
     */
    private static final Map<String /* 类路径 */, Object /* 对象实例 */> INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * 动态加载的类列表
     * <p>
     * TODO 使用注解和反射的方式动态添加
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);


    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("load all spi class.");
        for (Class<?> spiClass : LOAD_CLASS_LIST) {
            load(spiClass);
        }
    }


    /**
     * 加载某个类型的类
     * <p>
     * e.g. loadClazz = com.youyi.rpc.serializer.Serializer:
     * <p>
     * key=implClass
     * <ul>jdk=com.youyi.rpc.serializer.JdkSerializer</ul>
     * <ul>kryo=com.youyi.rpc.serializer.KryoSerializer</ul>
     * <ul>json=com.youyi.rpc.serializer.JsonSerializer</ul>
     * <ul>hessian=com.youyi.rpc.serializer.HessianSerializer</ul>
     *
     * @param loadClazz to be load class
     * @return key - impl class
     */
    public static Map<String, Class<?>> load(Class<?> loadClazz) {
        String loadClassName = loadClazz.getName();

        // 加载过的不重复加载（懒加载）
        if (LOADER_MAP.containsKey(loadClassName)) {
            return LOADER_MAP.get(loadClassName);
        }

        log.info("load spi class: {}", loadClassName);

        // 加载
        Map<String, Class<?>> keyImplClassMap = new HashMap<>();
        // custom > system
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClassName);
            // read resource
            for (URL resource : resources) {
                try (InputStreamReader isr = new InputStreamReader(resource.openStream());
                        BufferedReader bufReader = new BufferedReader(isr)) {
                    String line;
                    while ((line = bufReader.readLine()) != null) {
                        String[] keyImplClassArr = line.split(SPI_SEPARATOR);
                        if (keyImplClassArr.length < 2) {
                            continue;
                        }
                        String key = keyImplClassArr[0], implClassName = keyImplClassArr[1];
                        keyImplClassMap.put(key, Class.forName(implClassName));
                    }
                } catch (Exception e) {
                    log.error("spi resource load error, ", e);
                }
            }
        }
        LOADER_MAP.put(loadClassName, keyImplClassMap);
        return keyImplClassMap;
    }

    /**
     * 获取某个接口的实例
     *
     * @param loadClazz load class
     * @param key       implClass's key
     * @param <T>       {@link  T}
     * @return instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> loadClazz, String key) {
        String loadClazzName = loadClazz.getName();
        Map<String, Class<?>> keyImplClassMap = LOADER_MAP.get(loadClazzName);
        if (Objects.isNull(keyImplClassMap)) {
            throw new NoSuchLoadClassException(
                    String.format("SpiLoader don't load %s,", loadClazz));
        }
        if (!keyImplClassMap.containsKey(key)) {
            throw new NoSuchKeyException(
                    String.format("%s has no key=%s impl class type,", loadClazz, key));
        }

        // 获取到要加载的实现类型
        Class<?> implClazz = keyImplClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClazz.getName();
        if (!INSTANCE_CACHE.containsKey(implClassName)) {
            // 缓存中没有，需要加载
            try {
                INSTANCE_CACHE.put(implClassName, implClazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                String errMsg = String.format("create instance %s error", implClassName);
                throw new RuntimeException(errMsg, e);
            }
        }
        return (T) INSTANCE_CACHE.get(implClassName);
    }

}

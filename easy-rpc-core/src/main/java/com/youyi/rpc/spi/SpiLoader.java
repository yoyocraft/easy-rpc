package com.youyi.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.youyi.rpc.exception.NoSuchKeyException;
import com.youyi.rpc.exception.NoSuchLoadClassException;
import com.youyi.rpc.exception.RpcException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
     * SPI Class 所在目录
     */
    private static final String SPI_CLASS_DIR = "com.youyi.rpc";

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
    private static final Map<String /* 接口名 */, Map<String /* 实现类 key */, Class<?>/* 实现类 */>> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存，单例
     */
    private static final Map<String /* 类路径 */, Object /* 对象实例 */> INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * 动态加载的类列表
     */
    private static final Set<Class<?>> LOAD_CLASS_SET = new HashSet<>();


    /**
     * 加载所有类型，重量级操作，会删除原先加载的类缓存
     */
    public static void loadAll() {
        log.debug("load all spi class.");
        preLoadAll();
        doFindAllSpiClass(SPI_CLASS_DIR);
        for (Class<?> spiClass : LOAD_CLASS_SET) {
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
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                     | NoSuchMethodException e) {
                String errMsg = String.format("create instance %s error", implClassName);
                throw new RpcException(errMsg, e);
            }
        }
        return (T) INSTANCE_CACHE.get(implClassName);
    }

    private static void doFindAllSpiClass(String pkg) {
        String pkgPath = pkg.replaceAll("[.]", "/");
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(pkgPath);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(Objects.requireNonNull(is)))) {
            bufferedReader.lines().forEach(line -> {
                if (line.endsWith(".class")) {
                    try {
                        doGetClass(pkg, line);
                    } catch (ClassNotFoundException e) {
                        log.error("load spi class error", e);
                    }
                } else {
                    doFindAllSpiClass(pkg + "." + line);
                }
            });
        } catch (IOException e) {
            log.error("load spi class error", e);
        }
    }

    private static void preLoadAll() {
        LOAD_CLASS_SET.clear();
        INSTANCE_CACHE.clear();
    }

    private static void doGetClass(String pkg, String line) throws ClassNotFoundException {
        String className =
                pkg + "." + line.substring(0, line.lastIndexOf(".class"));
        Class<?> clazz = Class.forName(className);
        // 是接口，并且被 @SPI 注解修饰
        if (!checkSpiClass(clazz)) {
            return;
        }
        LOAD_CLASS_SET.add(clazz);
    }

    private static boolean checkSpiClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.isAnnotationPresent(SPI.class);
    }
}

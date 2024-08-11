package com.youyi.rpc.spi;

import cn.hutool.json.JSONUtil;
import com.youyi.rpc.serial.Serializer;
import io.vertx.core.impl.ConcurrentHashSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
class SpiLoaderTest {

    private static final Set<Class<?>> CLASS_SET = new ConcurrentHashSet<>();

    @Test
    void load() {
        SpiLoader.loadAll();
        String key = "custom_jdk";
        Serializer serializer = SpiLoader.getInstance(Serializer.class, key);
        log.info("{}: {}", key, serializer);
    }


    @Test
    void findAllClass() {
        findClass("com.youyi.rpc");
        CLASS_SET.forEach(clazz -> log.info("{}", clazz.getName()));
    }

    private void findClass(String pkg) {
        String pkgPath = pkg.replaceAll("[.]", "/");
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(pkgPath);
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(is)))) {
            bufferedReader.lines().forEach(line -> {
                if (line.endsWith(".class")) {
                    try {
                        String className =
                                pkg + "." + line.substring(0, line.lastIndexOf(".class"));
                        Class<?> clazz = Class.forName(className);
                        CLASS_SET.add(clazz);
                    } catch (ClassNotFoundException e) {
                        // 可以选择记录日志或者其他处理方式
                        throw new RuntimeException(e);
                    }
                } else {
                    findClass(pkg + "." + line);
                }
            });
        } catch (IOException e) {
            // 可以选择记录日志或者其他处理方式
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCheck() {
        Class<Serializer> clazz = Serializer.class;
        boolean check = clazz.isInterface() && clazz.isAnnotationPresent(SPI.class);
        log.info("{}", check);
    }

    @Test
    void testLoadAll() {
        SpiLoader.loadAll();
    }

    @Test
    void test_load() {
        Map<String, Class<?>> keyClassMap = SpiLoader.load(Serializer.class);
        log.info("{}", JSONUtil.toJsonStr(keyClassMap));
    }

}
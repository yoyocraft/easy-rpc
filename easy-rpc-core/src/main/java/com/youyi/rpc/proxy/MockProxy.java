package com.youyi.rpc.proxy;

import com.github.javafaker.Faker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * Mock 代理
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class MockProxy implements InvocationHandler {

    private static final Faker MOCK_DATA_FAKER = new Faker();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型，生成特定的默认值对象
        Class<?> returnType = method.getReturnType();
        log.debug("mock proxy return type: {}", returnType);
        return getMockResult(returnType);
    }

    private Object getMockResult(Class<?> type) {
        // 基本类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == byte.class) {
                return (byte) 0;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == float.class) {
                return 0.0F;
            } else if (type == double.class) {
                return 0.0D;
            }
            return 0;
        }
        // 对象类型
        if (type == String.class) {
            return MOCK_DATA_FAKER.lorem().word();
        }
        if (type == Date.class) {
            return MOCK_DATA_FAKER.date().birthday();
        }
        return null;
    }

}

package com.youyi.rpc.exception;

/**
 * 没有加载 loadClazz 的
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class NoSuchLoadClassException extends RuntimeException {

    public NoSuchLoadClassException() {
    }

    public NoSuchLoadClassException(String message) {
        super(message);
    }
}

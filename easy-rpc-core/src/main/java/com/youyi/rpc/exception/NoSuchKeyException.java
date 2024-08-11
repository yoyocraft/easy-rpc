package com.youyi.rpc.exception;

/**
 * 没有对应 key 异常
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
public class NoSuchKeyException extends RuntimeException {

    public NoSuchKeyException() {
    }

    public NoSuchKeyException(String message) {
        super(message);
    }
}

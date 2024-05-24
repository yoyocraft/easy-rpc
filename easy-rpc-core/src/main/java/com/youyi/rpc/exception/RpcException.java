package com.youyi.rpc.exception;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}

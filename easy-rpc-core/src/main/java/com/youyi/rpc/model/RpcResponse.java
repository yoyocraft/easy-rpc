package com.youyi.rpc.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class RpcResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Object data;
    private Class<?> dataType;
    private Throwable error;
    private String message;
}

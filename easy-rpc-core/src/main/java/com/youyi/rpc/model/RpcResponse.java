package com.youyi.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 响应体
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 异常信息
     */
    private Exception exception;

}

package com.youyi.rpc.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
@Builder
public class RpcRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String serviceName;
    private String methodName;
    private Class<?>[] paramsTypes;
    private Object[] args;

}

package com.youyi.rpc.model;

import com.youyi.rpc.constants.RpcConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC 请求体
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    @Builder.Default
    private String serviceVersion = RpcConstants.DEFAULT_SERVICE_VERSION;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] parameters;

}

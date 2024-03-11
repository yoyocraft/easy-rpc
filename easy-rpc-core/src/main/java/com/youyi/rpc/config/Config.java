package com.youyi.rpc.config;

import com.youyi.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class Config {

    private String name = "easy-rpc-framework";
    private String host = "127.0.0.1";
    private int port = 8080;
    private String version = "1.0.0";
    private boolean mock = false;
    private String serializer = SerializerKeys.HESSIAN;
    private RegistryConfig registryConfig = new RegistryConfig();
}

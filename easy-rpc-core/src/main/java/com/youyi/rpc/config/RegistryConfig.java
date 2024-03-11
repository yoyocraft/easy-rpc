package com.youyi.rpc.config;

import com.youyi.rpc.registry.RegistryKeys;
import lombok.Data;

/**
 * RPC 框架注册中心配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class RegistryConfig {


    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCD;

    /**
     * 注册中心地址
     */
    private String endpoints = "http://localhost:2380";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间 (ms)
     */
    private long timeout = 5000L;

}

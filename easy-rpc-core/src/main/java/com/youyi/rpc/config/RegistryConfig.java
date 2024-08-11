package com.youyi.rpc.config;

import com.youyi.rpc.registry.RegistryKeys;
import lombok.Data;

/**
 * RPC 框架注册中心配置
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String type = RegistryKeys.ETCD;

    /**
     * 注册中心地址，集群地址之间使用逗号分隔
     */
    private String endpoints = "http://localhost:2379";

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

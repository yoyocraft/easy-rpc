package com.youyi.rpc.model;

import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.lb.LoadBalancerKeys;
import lombok.Data;

/**
 * 服务注册信息，根据这个构建 ServiceMetadata
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Data
public class ServiceRegisterInfo<T> {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务实现类
     */
    private Class<? extends T> implClass;

    private String version;

    private String group;

    private String loadBalancer;


    public ServiceRegisterInfo(String serviceName, Class<? extends T> implClass) {
        this(serviceName, implClass, RpcConstants.DEFAULT_SERVICE_VERSION,
                RpcConstants.DEFAULT_SERVICE_GROUP, LoadBalancerKeys.ROUND_ROBIN);
    }

    public ServiceRegisterInfo(String serviceName, Class<? extends T> implClass, String version) {
        this(serviceName, implClass, version, RpcConstants.DEFAULT_SERVICE_GROUP,
                LoadBalancerKeys.ROUND_ROBIN);
    }

    public ServiceRegisterInfo(String serviceName, Class<? extends T> implClass, String version,
            String group) {
        this(serviceName, implClass, version, group, LoadBalancerKeys.ROUND_ROBIN);
    }

    public ServiceRegisterInfo(String serviceName, Class<? extends T> implClass, String version,
            String group, String loadBalancer) {
        this.serviceName = serviceName;
        this.implClass = implClass;
        this.version = version;
        this.group = group;
        this.loadBalancer = loadBalancer;
    }
}

package com.youyi.rpc.registry;

import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import java.util.List;

/**
 * 注册中心
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface Registry {


    /**
     * 注册中心初始化
     *
     * @param config registry config
     */
    void init(RegistryConfig config);

    /**
     * 服务端注册服务
     *
     * @param metadata 服务元信息
     */
    void register(ServiceMetadata metadata) throws Exception;

    /**
     * 服务端注销服务
     *
     * @param metadata 服务元信息
     */
    void deregister(ServiceMetadata metadata);

    /**
     * 消费端获取某服务的所有节点，服务发现
     *
     * @param serviceKey 服务键
     * @return 某服务下的所有服务节点元信息
     */
    List<ServiceMetadata> discovery(String serviceKey);


    /**
     * 注册中心销毁
     */
    void destroy();


}


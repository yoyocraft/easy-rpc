package com.youyi.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.youyi.rpc.constants.RpcConstant;
import lombok.Data;

/**
 * 服务元信息，需要注册到 注册中心 中
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class ServiceMetadata {

    private String serviceName;
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = RpcConstant.DEFAULT_SERVICE_GROUP;

    private String serviceHost;
    private int servicePort;


    /**
     * 获取服务键名
     *
     * @return service key
     */
    public String getServiceKey() {
        // TODO 后续可以扩展分组 name:version:group
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     * <p>
     * e.g. demo_service:1.0.0/localhost:8080
     *
     * @return 服务注册节点键名
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取完整的服务地址
     *
     * @return 完整服务地址
     */
    public String getServiceAddr() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

}

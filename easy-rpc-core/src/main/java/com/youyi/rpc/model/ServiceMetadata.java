package com.youyi.rpc.model;

import com.youyi.rpc.constants.RpcConstants;
import lombok.Data;

/**
 * 服务元信息，需要注册到注册中心中
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class ServiceMetadata {

    private String serviceName;
    private String serviceVersion = RpcConstants.DEFAULT_SERVICE_VERSION;
    private String serviceGroup = RpcConstants.DEFAULT_SERVICE_GROUP;
    private String serviceHost;
    private int servicePort;
    private long regTimestamp = System.currentTimeMillis();
}

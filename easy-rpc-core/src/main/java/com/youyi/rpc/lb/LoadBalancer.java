package com.youyi.rpc.lb;

import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.spi.SPI;
import java.util.List;
import java.util.Map;

/**
 * 服务端负载均衡器
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@SPI
public interface LoadBalancer {

    /**
     * 选择服务调用
     *
     * @param reqParams    请求参数
     * @param metadataList 可用服务列表
     * @return 服务调用信息
     */
    ServiceMetadata select(Map<String, Object> reqParams, List<ServiceMetadata> metadataList);
}

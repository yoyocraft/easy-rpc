package com.youyi.rpc.fault.tolerant;

import cn.hutool.core.collection.CollUtil;
import com.youyi.rpc.lb.LoadBalancer;
import com.youyi.rpc.lb.LoadBalancerFactory;
import com.youyi.rpc.lb.LoadBalancerKeys;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.server.tcp.VertxTcpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

/**
 * 失败自动切换
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public RpcResponse tolerant(Map<String, Object> context, Exception e)
            throws ExecutionException, InterruptedException, TimeoutException {
        // 自动切换实现，转移到其他节点
        List<ServiceMetadata> serviceMetadataList = (List<ServiceMetadata>) context.get(
                "serviceMetadataList");
        ServiceMetadata errorService = (ServiceMetadata) context.get("errorService");
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");

        log.error("[FailOverTolerantStrategy] access service {} failed，starting [fail over]",
                errorService.getServiceName());

        // 切换到其他节点
        serviceMetadataList.remove(errorService);
        if (CollUtil.isEmpty(serviceMetadataList)) {
            log.error("[FailOverTolerantStrategy] all service nodes is not available");
            return null;
        }
        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(
                LoadBalancerKeys.ROUND_ROBIN);
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetadata selectedService = loadBalancer.select(reqParams, serviceMetadataList);
        return VertxTcpClient.doRequest(rpcRequest, selectedService);
    }

}

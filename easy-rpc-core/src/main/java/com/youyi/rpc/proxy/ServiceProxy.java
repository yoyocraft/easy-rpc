package com.youyi.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.constants.RpcConstants;
import com.youyi.rpc.fault.retry.RetryStrategy;
import com.youyi.rpc.fault.retry.RetryStrategyFactory;
import com.youyi.rpc.fault.tolerant.TolerantStrategy;
import com.youyi.rpc.fault.tolerant.TolerantStrategyFactory;
import com.youyi.rpc.lb.LoadBalancer;
import com.youyi.rpc.lb.LoadBalancerFactory;
import com.youyi.rpc.model.RpcRequest;
import com.youyi.rpc.model.RpcResponse;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.registry.Registry;
import com.youyi.rpc.registry.RegistryFactory;
import com.youyi.rpc.serializer.Serializer;
import com.youyi.rpc.serializer.SerializerFactory;
import com.youyi.rpc.server.tcp.VertxTcpClient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务代理，基于 JDK 动态代理
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        ApplicationConfig applicationConfig = RpcApplication.resolve();
        final Serializer serializer = SerializerFactory.getSerializer(
                applicationConfig.getSerializer());

        log.debug("service proxy use serializer: {}", serializer);

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        Registry registry = RegistryFactory.getRegistry(
                applicationConfig.getRegistry().getRegistry());
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName(serviceName);
        serviceMetadata.setServiceVersion(RpcConstants.DEFAULT_SERVICE_VERSION);
        List<ServiceMetadata> serviceMetadataList = registry.discovery(
                serviceMetadata.getServiceKey());
        if (CollUtil.isEmpty(serviceMetadataList)) {
            throw new RuntimeException("there are no registry!");
        }

        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(
                applicationConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetadata selectedService = loadBalancer.select(reqParams, serviceMetadataList);

        RpcResponse rpcResponse;
        try {
            // 重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy(
                    applicationConfig.getTolerant().getRetry());
            // 发送 TCP 请求
            rpcResponse = retryStrategy.retry(
                    () -> VertxTcpClient.doRequest(rpcRequest, selectedService));
        } catch (Exception e) {
            // 容错上下文
            Map<String, Object> context = Map.of("serviceMetadataList", serviceMetadataList,
                    "errorService", selectedService, "rpcRequest", rpcRequest);
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getTolerantStrategy(
                    applicationConfig.getTolerant().getTolerant());
            rpcResponse = tolerantStrategy.tolerant(context, e);
        }
        return rpcResponse.getData();
    }
}

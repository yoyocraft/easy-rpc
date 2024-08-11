package com.youyi.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.exception.RpcException;
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
import com.youyi.rpc.serial.Serializer;
import com.youyi.rpc.serial.SerializerFactory;
import com.youyi.rpc.server.tcp.VertxTcpClient;
import com.youyi.rpc.util.MetadataUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务代理，基于 JDK 动态代理
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    private final String version;
    private final String group;
    private final String loadBalancer;
    private final long timeout;

    public ServiceProxy(String version, String group, String loadBalancer, long timeout) {
        this.version = version;
        this.group = group;
        this.loadBalancer = loadBalancer;
        this.timeout = timeout;
    }

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
                applicationConfig.getRegistry().getType());
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName(serviceName);
        serviceMetadata.setServiceVersion(version);
        serviceMetadata.setServiceGroup(group);
        List<ServiceMetadata> serviceMetadataList = registry.discovery(
                MetadataUtil.getServiceKey(serviceMetadata));
        if (CollUtil.isEmpty(serviceMetadataList)) {
            throw new RpcException("there are no registry services!");
        }

        // 负载均衡
        String loadBalancerType =
                StrUtil.isBlank(loadBalancer) ? applicationConfig.getLoadBalancer() : loadBalancer;
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(loadBalancerType);
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
                    () -> VertxTcpClient.doRequest(rpcRequest, selectedService, timeout));
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

package com.youyi.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.youyi.rpc.RpcApplication;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.constants.RpcConstant;
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
import java.util.List;
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
        Config config = RpcApplication.resolve();
        final Serializer serializer = SerializerFactory.getSerializer(
                config.getSerializer());

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
                config.getRegistry().getRegistry());
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName(serviceName);
        serviceMetadata.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetadata> serviceMetadataList = registry.discovery(
                serviceMetadata.getServiceKey());
        if (CollUtil.isEmpty(serviceMetadataList)) {
            throw new RuntimeException("there are no registry!");
        }
        // TODO 暂时先取第一个作为服务地址
        ServiceMetadata selectedService = serviceMetadataList.get(0);

        // 发送 TCP 请求
        RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedService);
        return rpcResponse.getData();
    }
}

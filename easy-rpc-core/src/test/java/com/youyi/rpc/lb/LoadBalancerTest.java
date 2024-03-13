package com.youyi.rpc.lb;

import com.youyi.rpc.model.ServiceMetadata;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
class LoadBalancerTest {

    // final LoadBalancer loadBalancer = new RandomLoadBalancer();
    // final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
    final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    void select() {
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("methodName", "Banana");

        List<ServiceMetadata> metadataList = getServiceMetadataList();

        ServiceMetadata metadata;
        metadata = loadBalancer.select(reqParams, metadataList);
        log.info("[1st] metadata:{}", metadata);

        metadata = loadBalancer.select(reqParams, metadataList);
        log.info("[2nd] metadata:{}", metadata);

        metadata = loadBalancer.select(reqParams, metadataList);
        log.info("[3rd] metadata:{}", metadata);

    }

    private static List<ServiceMetadata> getServiceMetadataList() {
        ServiceMetadata metadata1 = new ServiceMetadata();
        metadata1.setServiceName("service-1");
        metadata1.setServiceVersion("1.0.0");
        metadata1.setServiceHost("localhost");
        metadata1.setServicePort(1111);

        ServiceMetadata metadata2 = new ServiceMetadata();
        metadata2.setServiceName("service-2");
        metadata2.setServiceVersion("1.0.0");
        metadata2.setServiceHost("localhost");
        metadata2.setServicePort(2222);

        ServiceMetadata metadata3 = new ServiceMetadata();
        metadata3.setServiceName("service-3");
        metadata3.setServiceVersion("1.0.0");
        metadata3.setServiceHost("localhost");
        metadata3.setServicePort(3333);
        return List.of(metadata1, metadata2, metadata3);
    }
}
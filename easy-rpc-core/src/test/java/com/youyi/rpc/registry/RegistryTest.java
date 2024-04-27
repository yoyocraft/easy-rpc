package com.youyi.rpc.registry;


import com.youyi.rpc.config.RegistryConfig;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.util.MetadataUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
class RegistryTest {

    private final Registry registry = new EtcdRegistry();

    @BeforeEach
    void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setEndpoints("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    void register() throws Exception {
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName("service-1");
        serviceMetadata.setServiceVersion("1.0.0");
        serviceMetadata.setServiceHost("localhost");
        serviceMetadata.setServicePort(1234);
        registry.register(serviceMetadata);

        serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName("service-1");
        serviceMetadata.setServiceVersion("1.0.0");
        serviceMetadata.setServiceHost("localhost");
        serviceMetadata.setServicePort(1235);
        registry.register(serviceMetadata);

        serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName("service-1");
        serviceMetadata.setServiceVersion("2.0.0");
        serviceMetadata.setServiceHost("localhost");
        serviceMetadata.setServicePort(1234);
        registry.register(serviceMetadata);

    }

    @Test
    void deregister() {
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName("service-1");
        serviceMetadata.setServiceVersion("1.0.0");
        serviceMetadata.setServiceHost("localhost");
        serviceMetadata.setServicePort(1234);
        registry.unregister(serviceMetadata);
    }

    @Test
    void discovery() {
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setServiceName("service-1");
        serviceMetadata.setServiceVersion("1.0.0");
        String serviceKey = MetadataUtil.getServiceKey(serviceMetadata);
        List<ServiceMetadata> serviceMetadataList = registry.discovery(serviceKey);
        log.info("discovery result: {}", serviceMetadataList);
        Assertions.assertNotNull(serviceMetadataList);
    }

    @Test
    void destroy() {
    }

    @Test
    void heartBeat() throws Exception {
        // init 方法中已经执行心跳检测了
        register();
        // 阻塞 1min
        Thread.sleep(60 * 1000L);
    }
}
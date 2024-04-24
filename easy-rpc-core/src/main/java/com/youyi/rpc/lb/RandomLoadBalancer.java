package com.youyi.rpc.lb;

import com.youyi.rpc.model.ServiceMetadata;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

/**
 * 随机负载均衡器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class RandomLoadBalancer implements LoadBalancer {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @Override
    public ServiceMetadata select(Map<String, Object> reqParams,
            List<ServiceMetadata> metadataList) {
        if (metadataList.isEmpty()) {
            return null;
        }

        // 只有一个服务，不随机
        int size = metadataList.size();
        if (size == 1) {
            return metadataList.get(0);
        }

        int index = RANDOM.nextInt(size);
        return metadataList.get(index);
    }
}

package com.youyi.rpc.lb;

import com.youyi.rpc.model.ServiceMetadata;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * 轮询负载均衡器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 当前选择的服务索引
     */
    private final AtomicInteger CURR_INDEX = new AtomicInteger(0);

    @Override
    public ServiceMetadata select(Map<String, Object> reqParams,
            List<ServiceMetadata> metadataList) {
        if (metadataList.isEmpty()) {
            return null;
        }

        // 只有一个服务，无需轮询
        int size = metadataList.size();
        if (size == 1) {
            return metadataList.get(0);
        }

        int index = CURR_INDEX.getAndIncrement() % size;
        return metadataList.get(index);
    }
}

package com.youyi.rpc.lb;

import com.youyi.rpc.model.ServiceMetadata;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;

/**
 * 一致性 Hash 负载均衡
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ConsistentHashLoadBalancer implements LoadBalancer {

    @Override
    public ServiceMetadata select(Map<String, Object> reqParams,
            List<ServiceMetadata> metadataList) {
        ConsistentHash consistentHash = new ConsistentHash(metadataList);
        return consistentHash.gerService(reqParams);
    }


    /**
     * 一致性 Hash
     * <p>
     * {@link java.util.TreeMap}
     */
    static class ConsistentHash {

        /**
         * 虚拟节点数
         */
        private static final int VIRTUAL_NODES_SIZE = 100;

        /**
         * 一致性 Hash 环
         */
        private static final TreeMap<Integer, ServiceMetadata> VIRTUAL_NODES = new TreeMap<>();

        private final List<ServiceMetadata> metadataList;

        public ConsistentHash(List<ServiceMetadata> metadataList) {
            this.metadataList = metadataList;
            init();
        }

        private void init() {
            for (ServiceMetadata metadata : metadataList) {
                for (int i = 0; i < VIRTUAL_NODES_SIZE; i++) {
                    String addr = metadata.getServiceAddr();
                    // 计算虚拟节点的 Hash 值
                    int hash = getHash(addr + "#" + i);
                    VIRTUAL_NODES.put(hash, metadata);
                }
            }
        }

        /**
         * 获取服务
         *
         * @param clientInfo Hash 参数
         * @return ServiceMetadata
         */
        public ServiceMetadata gerService(Object clientInfo) {
            int hash = getHash(clientInfo);

            // 选择最接近且大于等于 clientInfo Hash 值的虚拟节点
            Map.Entry<Integer, ServiceMetadata> entry
                    = VIRTUAL_NODES.ceilingEntry(hash);
            if (entry == null) {
                // 如果不存在大于等于 clientInfo Hash 值的虚拟节点，返回环首部节点
                entry = VIRTUAL_NODES.firstEntry();
            }

            return entry.getValue();
        }

        /**
         * FNV1_32_HASH 算法
         *
         * @param obj object
         * @return hash
         */
        private static int getHash(Object obj) {
            final int p = 16777619;
            int hash = (int) 2166136261L;
            String str = obj.toString();
            for (int i = 0; i < str.length(); i++) {
                hash = (hash ^ str.charAt(i)) * p;
                hash += hash << 13;
                hash ^= hash >> 7;
                hash += hash << 3;
                hash ^= hash >> 17;
                hash += hash << 5;
                if (hash < 0) {
                    hash = Math.abs(hash);
                }
            }
            return hash;
        }

    }
}

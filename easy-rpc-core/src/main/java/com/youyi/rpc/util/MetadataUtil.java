package com.youyi.rpc.util;

import cn.hutool.core.util.StrUtil;
import com.youyi.rpc.model.ServiceMetadata;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetadataUtil {

    /**
     * 获取服务键名
     * <p>
     * e.g. demo_service:1.0.0
     *
     * @return service key
     */
    public static String getServiceKey(ServiceMetadata metadata) {
        // TODO 后续可以扩展分组 name:version:group
        return String.format("%s:%s", metadata.getServiceName(), metadata.getServiceVersion());
    }

    /**
     * 获取服务注册节点键名
     * <p>
     * e.g. demo_service:1.0.0/localhost:8080
     *
     * @return 服务注册节点键名
     */
    public static String getServiceNodeKey(ServiceMetadata metadata) {
        return String.format("%s/%s:%s", getServiceKey(metadata), metadata.getServiceHost(),
                metadata.getServicePort());
    }

    /**
     * 获取服务注册节点键名
     * <p>
     * e.g. demo_service:1.0.0:localhost:8080
     *
     * @return 服务注册节点键名
     */
    public static String getListServiceNodeKey(ServiceMetadata metadata) {
        return String.format("%s:%s:%s", getServiceKey(metadata), metadata.getServiceHost(),
                metadata.getServicePort());
    }

    /**
     * 获取服务键名
     */
    public static String getServiceKey(String serviceNodeKey) {
        return serviceNodeKey.split("/")[0];
    }

    /**
     * 获取完整的服务地址
     * <p>
     * TODO 待修复，万一是 https 等情况呢？
     *
     * @return 完整服务地址
     */
    public static String getServiceAddr(ServiceMetadata metadata) {
        String serviceHost = metadata.getServiceHost();
        int servicePort = metadata.getServicePort();
        if (!StrUtil.startWith(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

}

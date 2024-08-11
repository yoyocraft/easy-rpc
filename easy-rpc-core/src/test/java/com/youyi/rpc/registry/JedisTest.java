package com.youyi.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.youyi.rpc.model.ServiceMetadata;
import com.youyi.rpc.util.MetadataUtil;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class JedisTest {

    private Jedis jedis;

    @BeforeEach
    void init() {
        jedis = new Jedis("http://localhost:6379");
        jedis.auth("local_redis");
    }

    @Test
    void testListAdd() {
        String key = "service";
        String val1 = "localhost:8080", val2 = "youyi.icu", val3 = "codejuzi.icu";
        jedis.lpush(key, val1, val2, val3);
        jedis.expire(key, 100L);

        // 查询
        List<String> res = jedis.lrange(key, 0, -1);
        log.info("values -> {}", res);
    }

    @Test
    void testListDel() {
        String key = "service";
        String val1 = "localhost:8080", val2 = "youyi.icu", val3 = "codejuzi.icu";
        jedis.lpush(key, val1, val2, val3);
        jedis.expire(key, 100L);

        // 查询
        List<String> res = jedis.lrange(key, 0, -1);
        log.info("values -> {}", res);

        // 删除
        long del = jedis.lrem(key, -1, "localhost:8080");
        log.info("del -> {}", del);
    }

    @Test
    void testConn() {
        try (Jedis jedis = new Jedis("http://localhost:6379")) {
            jedis.auth("local_redis");
            String res = jedis.ping();
            log.info("ping -> {}", res);
        }
    }

    @Test
    void testRegister() {
        ServiceMetadata metadata1 = new ServiceMetadata();
        metadata1.setServiceName("userService");
        metadata1.setServiceHost("localhost");
        metadata1.setServicePort(8080);

        ServiceMetadata metadata2 = new ServiceMetadata();
        metadata2.setServiceName("userService");
        metadata2.setServiceHost("localhost2");
        metadata2.setServicePort(8080);

        String nodeKey1 = MetadataUtil.getListServiceNodeKey(metadata1);
        String nodeKey2 = MetadataUtil.getListServiceNodeKey(metadata2);

        jedis.set(nodeKey1, JSONUtil.toJsonStr(metadata1));
        jedis.set(nodeKey2, JSONUtil.toJsonStr(metadata2));
        jedis.expire(nodeKey1, 100L);
        jedis.expire(nodeKey2, 100L);
    }

    @Test
    void testUnregister() {
        ServiceMetadata metadata = new ServiceMetadata();
        metadata.setServiceName("userService");
        metadata.setServiceHost("localhost");
        metadata.setServicePort(8080);

        String nodeKey = MetadataUtil.getListServiceNodeKey(metadata);
        jedis.unlink(nodeKey);
    }

    @Test
    void testDiscovery() {
        ServiceMetadata metadata = new ServiceMetadata();
        metadata.setServiceName("userService");
        metadata.setServiceHost("localhost");
        metadata.setServicePort(8080);

        String serviceKey = MetadataUtil.getServiceKey(metadata);
        Set<String> keys = jedis.keys(serviceKey + ":*");
        List<String> res = jedis.mget(keys.toArray(new String[0]));

        log.info("values -> {}", res);
    }

    @AfterEach
    void close() {
        jedis.close();
    }

}

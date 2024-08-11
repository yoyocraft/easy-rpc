package com.youyi.rpc.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
class MetadataUtilTest {

    @Test
    void getServiceNodeKey() {
        String serviceNodeKey = "demo_service:1.0.0/codejuzi.icu/iterator";
        String exceptServiceKey = "demo_service:1.0.0";
        String serviceKey = MetadataUtil.getServiceKey(serviceNodeKey);
        assertEquals(exceptServiceKey, serviceKey);
    }
}
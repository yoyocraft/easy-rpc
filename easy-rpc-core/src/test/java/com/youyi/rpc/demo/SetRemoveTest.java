package com.youyi.rpc.demo;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class SetRemoveTest {

    @Test
    void testRemove() {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        set.add("c");
        log.info("set:{}", set);
        for (String val : set) {
            if ("b".equals(val)) {
                set.remove(val);
            }
        }
        log.info("set:{}", set);
    }

}

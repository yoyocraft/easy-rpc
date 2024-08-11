package com.youyi.example;

import com.youyi.example.common.model.User;
import com.youyi.example.common.service.UserService;
import com.youyi.rpc.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
@Service
@RpcService(version = "3.0.0", group = "test-spring")
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(String name) {
        log.info("getUser: {}", name);
        // 模拟网络延迟
        try {
            Thread.sleep(3100);
        } catch (InterruptedException e) {
            log.error("sleep error", e);
        }
        return new User(name + "-proxy[3.0.0, test-spring]");
    }
}

package com.youyi.example.provider;

import com.youyi.example.common.model.User;
import com.youyi.example.common.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现类
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(String name) {
        log.info("provider receive user name: {}", name);
        // 模拟超时
        try {
            Thread.sleep(3100);
        } catch (InterruptedException e) {
            log.error("provider sleep error", e);
        }
        return new User("[2.0.0] &Provider& " + name);
    }
}

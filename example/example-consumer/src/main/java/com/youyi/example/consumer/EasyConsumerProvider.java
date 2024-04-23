package com.youyi.example.consumer;

import com.youyi.example.common.model.User;
import com.youyi.example.common.service.UserService;
import com.youyi.rpc.proxy.ServiceProxyFactory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class EasyConsumerProvider {

    private static final Logger logger = LoggerFactory.getLogger(EasyConsumerProvider.class);
    private static final User DEFAULT_USER = new User("default_user");

    public static void main(String[] args) {
        main0();
    }

    public static void main0() {
        // 获取 UserService 的代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        String name = "youyi";
        User user = Optional.ofNullable(userService.getUser(name)).orElse(DEFAULT_USER);
        logger.info("consumer receive user: {}", user);
    }

}

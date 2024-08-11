package com.youyi.example.consumer;

import com.youyi.example.common.model.User;
import com.youyi.example.common.service.UserService;
import com.youyi.rpc.bootstrap.ConsumerBootstrap;
import com.youyi.rpc.proxy.ServiceProxyFactory;
import java.util.Objects;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

/**
 * 简易服务消费者示例
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class ConsumerExample {

    public static void main(String[] args) {
        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取 UserService 实现类对象
        UserService userService = getUserService();

        log.info("mock data = {}", userService.getNumber());

        String name = "youyi";
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < 3; i++) {
            User newUser = userService.getUser(name);
            if (Objects.nonNull(newUser)) {
                log.info("new user name: {}", newUser.getName());
            } else {
                log.info("user is null");
            }
            sc.next();
        }
        sc.close();
    }

    private static UserService getUserService() {
        return ServiceProxyFactory.getProxy(UserService.class,
                "2.0.0", "test");
    }
}

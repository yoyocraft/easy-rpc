package com.youyi.example;

import com.youyi.example.common.model.User;
import com.youyi.example.common.service.UserService;
import com.youyi.rpc.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
@Service
public class ExampleServiceImpl {

    @RpcReference(version = "3.0.0", group = "test-spring", timeout = 5000)
    private UserService userService;

    public void test() {
        User newUser = userService.getUser("youyi");
        // log.info("new username: {}", newUser.getName());
        System.out.println("new username: " + newUser.getName());
    }

}

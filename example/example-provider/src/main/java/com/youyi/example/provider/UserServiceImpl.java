package com.youyi.example.provider;

import com.youyi.example.common.model.User;
import com.youyi.example.common.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User getUser(String name) {
        User user = new User(name);
        logger.info("producer create user: {}", user);
        return user;
    }
}

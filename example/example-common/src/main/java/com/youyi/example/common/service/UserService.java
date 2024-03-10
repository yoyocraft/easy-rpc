package com.youyi.example.common.service;

import com.youyi.example.common.model.User;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user user query info
     * @return user info
     */
    User getUser(User user);

    /**
     * 获取一个数字
     *
     * @return number
     */
    default short getNumber() {
        return 1;
    }

}

package com.youyi.example.common.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@lombok.Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    public void setName(String name) {
        this.name = name;
    }
}

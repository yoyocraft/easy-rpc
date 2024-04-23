package com.youyi.example.common.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
@Setter
@Getter
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    public User(String name) {
        this.name = name;
    }
}

package com.youyi.example;

import com.youyi.rpc.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@SpringBootApplication
@EnableRpc
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

}

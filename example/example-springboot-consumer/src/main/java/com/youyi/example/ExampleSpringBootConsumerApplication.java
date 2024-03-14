package com.youyi.example;

import com.youyi.rpc.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="https://github.com/dingxinliang88">codejuzi</a>
 */
@SpringBootApplication
@EnableRpc(needServer = false)
public class ExampleSpringBootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringBootConsumerApplication.class, args);
    }

}

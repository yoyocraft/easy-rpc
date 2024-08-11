package com.youyi.rpc.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志某接口是 SPI 接口
 *
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface SPI {

}

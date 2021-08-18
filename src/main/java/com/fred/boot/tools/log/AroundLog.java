package com.fred.boot.tools.log;

import java.lang.annotation.*;

/**
 * @author Fred
 * @date 2021/8/7 14:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AroundLog {
    String flag() default "";
}

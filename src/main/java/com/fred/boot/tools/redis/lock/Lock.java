package com.fred.boot.tools.redis.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Fred
 * @date 2021/8/3 16:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Lock {
    String value();

    String param() default "";

    long waitTime() default 30L;

    long leaseTime() default 100L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    LockType type() default LockType.FAIR;
}

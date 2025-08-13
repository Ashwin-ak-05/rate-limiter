package org.library.ratelimiter.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    String strategy() default "TOKEN_BUCKET";
    String api() default "USER";
}

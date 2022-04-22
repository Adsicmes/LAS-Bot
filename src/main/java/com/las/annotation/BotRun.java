package com.las.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotRun {

    String qq() default "";

    String qqAuth() default "";

    int port() default 8888;

    String miraiUrl() default "http://localhost:5700";

    String botServer() default "/cq/getMsg";

    String webpath() default "classes/static";

}

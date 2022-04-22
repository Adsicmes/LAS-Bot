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

    String miraiUrl() default "";

    String botServer() default "";

    int port() default 6666;

    String webpath() default "target/classes/static";

}

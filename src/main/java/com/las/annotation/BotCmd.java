package com.las.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dullwolf
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface BotCmd {

    /**
     * 功能名称
     */
    String funName();

    /**
     * 功能权限
     */
    int funWeight() default 0;

    /**
     * 是否根据指令匹配，默认true，有部分指令不需要，例如鉴定色图指令
     */
    boolean isMatch() default true;

}

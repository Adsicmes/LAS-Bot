package com.las.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dullwolf
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WxBotCmd {

    /**
     * 是否根据指令匹配，默认true，有部分指令不需要，可以改为false
     */
    boolean isMatch() default true;

}

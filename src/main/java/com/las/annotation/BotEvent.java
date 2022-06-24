package com.las.annotation;

import com.las.enums.MsgCallBackEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dullwolf
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotEvent {

    /**
     * QQ事件名称
     */
    MsgCallBackEnum event();

}

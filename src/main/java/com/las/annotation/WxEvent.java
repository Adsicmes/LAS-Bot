package com.las.annotation;

import com.las.enums.WxMsgCallBackEnum;
import org.springframework.context.annotation.Scope;
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
@Scope("prototype")
public @interface WxEvent {

    /**
     * 微信事件名称
     */
    WxMsgCallBackEnum event();

}

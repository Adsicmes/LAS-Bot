package com.las.annotation;

import com.las.config.AppConfigs;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author dullwolf
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({AppConfigs.class})
public @interface EnableMirai {

    /**
     * 静态资源路径
     */
    String webPath() default "target/classes/static";

    /**
     * 超管QQ
     */
    String superQQ();

    /**
     * 机器人QQ
     */
    String botQQ();

    /**
     * mirai设置的密钥
     */
    String keyAuth();

    /**
     * 本项目服务端口
     */
    int botPort() default 8888;

    /**
     * mirai服务url和端口
     */
    String miRaiUrl() default "http://localhost:5700";

    /**
     * mirai对接http接口
     */
    String botServer() default "/cq/getMsg";

    /**
     * 微信机器人服务url和端口（websocket）
     */
    String wxServerUrl() default "ws://127.0.0.1:5555";

    /**
     * 默认不启动微信机器人
     */
    boolean isEnableWxBot() default false;

}

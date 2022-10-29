package com.las.enums;

import com.las.annotation.WxEvent;
import com.las.common.Constant;
import com.las.strategy.wxhandle.AbstractWxBotMsgHandler;
import com.las.strategy.wxhandle.WxFriendMsgHandler;
import com.las.utils.ClassUtil;

import java.util.Set;

/**
 * @author dullwolf
 */
public enum WxMsgCallBackEnum {
    // 文档暂无

    // 消息事件
    // 好友消息
    WX_FRIEND_MSG(1, WxFriendMsgHandler.class.getName()),
    ;


    private int eventType;
    private String className;

    WxMsgCallBackEnum(int eventType, String className) {
        this.eventType = eventType;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public int getEventType() {
        return eventType;
    }

//    public static String getClassNameByType(int eventType) {
//        for (WxMsgCallBackEnum msgCallBackStrategyEnum : WxMsgCallBackEnum.values()) {
//            if (msgCallBackStrategyEnum.eventType == eventType) {
//                return msgCallBackStrategyEnum.getClassName();
//            }
//        }
//        return null;
//    }

    public static String getClassNameByType(int eventType) {
        String className = null;
        for (WxMsgCallBackEnum msgCallBackStrategyEnum : WxMsgCallBackEnum.values()) {
            if (msgCallBackStrategyEnum.eventType == eventType) {
                className = msgCallBackStrategyEnum.getClassName();
                if (!Constant.NONE.equals(className)) {
                    break;
                }
            }
        }
        // 若用户自己有事件，扫描用户定义的
        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, WxEvent.class);
        for (Class<?> c : classSet) {
            WxEvent botEvent = c.getDeclaredAnnotation(WxEvent.class);
            if (botEvent.event().getEventType() == eventType) {
                Class<?> superclass = c.getSuperclass();
                String superClassName = superclass.getName();
                String botMsgClassName = AbstractWxBotMsgHandler.class.getName();
                if(superClassName.equals(botMsgClassName)){
                    className = c.getName();
                }
            }
        }
        return className;
    }

}

package com.las.enums;

import com.las.strategy.wxhandle.WxFriendMsgHandler;

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

    public static String getClassNameByType(int eventType) {
        for (WxMsgCallBackEnum msgCallBackStrategyEnum : WxMsgCallBackEnum.values()) {
            if (msgCallBackStrategyEnum.eventType == eventType) {
                return msgCallBackStrategyEnum.getClassName();
            }
        }
        return null;
    }

}

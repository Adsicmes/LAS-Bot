package com.las.enums;

import com.las.strategy.handle.FriendMsgHandler;
import com.las.strategy.handle.GroupMsgHandler;

public enum MsgCallBackEnum {

    GROUP_MSG_CALL_BACK("GroupMessage", GroupMsgHandler.class.getName()),
    FRIEND_MSG_CALL_BACK("FriendMessage", FriendMsgHandler.class.getName());
//    TEMP_MSG_CALL_BACK("TempMessage","类名路径"),
//    STRANGER_MSG_CALL_BACK("StrangerMessage","类名路径"),
//    OTHER_MSG_CALL_BACK("OtherClientMessage","类名路径"),
//    BOT_ONLINE_CALL_BACK("BotOnlineEvent","类名路径");

    private String eventName;
    private String className;

    MsgCallBackEnum(String eventName,String className) {
        this.eventName = eventName;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public static String getClassNameByEvent(String eventName) {
        for (MsgCallBackEnum msgCallBackStrategyEnum : MsgCallBackEnum.values()) {
            if (msgCallBackStrategyEnum.eventName.equals(eventName)) {
                return msgCallBackStrategyEnum.getClassName();
            }
        }
        return null;
    }

}

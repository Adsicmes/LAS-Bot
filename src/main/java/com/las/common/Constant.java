package com.las.common;

public class Constant {

    /**
     * mirai消息临时会话
     */
    public static volatile String session;

    /**
     * QQ用户默认权限
     */
    public static final int DEFAULT_PERMISSION = 0;

    /**
     * CQ消息类型
     */
    public static final int MESSAGE_TYPE_PRIVATE = 0;
    public static final int MESSAGE_TYPE_GROUP = 1;
    public static final int MESSAGE_TYPE_DISCUSS = 2;

    /**
     * 消息前缀
     */
    public static final String DEFAULT_PRE = "#";
}

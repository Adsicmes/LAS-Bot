package com.las.common;

import java.util.regex.Pattern;

/**
 * @author dullwolf
 */
public class Constant {

    /**
     * 消息临时会话
     */
    public static volatile String session;

    /**
     * QQ用户默认权限
     */
    public static final int DEFAULT_PERMISSION = 0;

    /**
     * 超管QQ默认权限
     */
    public static final int SUPER_PERMISSION = 999;

    /**
     * 管理员临界值权限
     */
    public static final int ADMIN_PERMISSION = 996;

    /**
     * CQ消息类型
     */
    public static final int MESSAGE_TYPE_PRIVATE = 0;
    public static final int MESSAGE_TYPE_GROUP = 1;
    public static final int MESSAGE_TYPE_DISCUSS = 2;

    /**
     * 指令执行优先级
     */
    public static final int DEFAULT_PRIORITY = 10;
    public static final int MIN_PRIORITY = 0;
    public static final int MAX_PRIORITY = 999;

    /**
     * 消息前缀
     */
    public static final String DEFAULT_PRE = "#";

    /**
     * 空定义
     */
    public static final String KONG = "空";
    public static final String NONE = "NONE";

    /**
     * 权限赋能
     */
    public static final String PERMISSION_UPDATE = "权限更新";
    public static final String PERMISSION_ENABLE = "权限赋能";
    public static final String INIT_PERMISSION = "初次初始化权限赋能";

    /**
     * 指令去多个空格
     */
    public static Pattern PATTERN_ONE_SPACE = Pattern.compile("\\s+");
}

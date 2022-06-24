package com.las.enums;

import com.las.annotation.BotEvent;
import com.las.common.Constant;
import com.las.strategy.handle.*;
import com.las.utils.ClassUtil;

import java.util.Set;

/**
 * @author dullwolf
 */
public enum MsgCallBackEnum {
    // 文档 ↓
    // https://docs.mirai.mamoe.net/mirai-api-http/api/EventType.html

    // 消息事件
    // 群消息
    GROUP_MSG("GroupMessage", GroupMsgHandler.class.getName()),
    // 好友消息
    FRIEND_MSG("FriendMessage", FriendMsgHandler.class.getName()),
    //临时消息
    TEMP_MSG("TempMessage", Constant.NONE),
    //陌生人消息
    STRANGER_MSG("StrangerMessage", Constant.NONE),
    //其他客户端消息
    OTHER_CLIENT_MSG("OtherClientMessage", Constant.NONE),


    // bot自身事件
    // bot上线
    BOT_ONLINE_MSG("BotOnlineEvent", Constant.NONE),
    //bot主动离线
    BOT_OFFLINE_ACTIVE_MSG("BotOfflineEventActive", Constant.NONE),
    //被挤下线
    BOT_OFFLINE_FORCE_MSG("BotOfflineEventForce", Constant.NONE),
    // bot掉线
    BOT_OFFLINE_DROPED_MSG("BotOfflineEventDropped", Constant.NONE),
    // bot主动重新登录
    BOT_RELOGIN_MSG("BotReloginEvent", Constant.NONE),


    // 好友事件
    // 好友输入状态改变
    FRIEND_INPUT_STATUS_CHANGED_MSG("FriendInputStatusChangedEvent", Constant.NONE),
    // 好友昵称改变
    FRIEND_NICK_CHANGED_MSG("FriendNickChangedEvent", Constant.NONE),


    // 群事件
    //Bot在群里的权限被改变. 操作人一定是群主
    BOT_GROUP_PERMISSION_CHANGE_MSG("BotGroupPermissionChangeEvent", Constant.NONE),
    // Bot被禁言
    BOT_MUTE_MSG("BotMuteEvent", "类包路径"),
    // Bot被取消禁言
    BOT_UNMUTE_MSG("BotUnmuteEvent", Constant.NONE),
    // Bot加入了一个新群
    BOT_JOIN_GROUP_MSG("BotJoinGroupEvent", Constant.NONE),
    // Bot主动退群
    BOT_LEAVE_ACTIVE_MSG("BotLeaveEventActive", Constant.NONE),
    // Bot被踢出一个群
    BOT_LEAVE_KICK_MSG("BotLeaveEventKick", Constant.NONE),
    // 群消息撤回
    GROUP_RECALL_MSG("GroupRecallEvent", Constant.NONE),
    // 好友消息撤回
    FRIEND_RECALL_MSG("FriendRecallEvent", Constant.NONE),
    // 戳一戳事件
    NUDGE_MSG("NudgeEvent", Constant.NONE),
    // 某群名改变
    GROUP_NAME_CHANGE_MSG("GroupNameChangeEvent", Constant.NONE),
    // 某群入群公告改变
    GROUP_ENTRANCE_ANNOUNCEMENT_CHANGE_MSG("GroupEntranceAnnouncementChangeEvent", Constant.NONE),
    // 全员禁言
    GROUP_MUTE_ALL_MSG("GroupMuteAllEvent", Constant.NONE),
    // 匿名聊天
    GROUP_ALLOW_ANONYMOUS_CHAT_MSG("GroupAllowAnonymousChatEvent", Constant.NONE),
    // 坦白说
    GROUP_ALLOW_CONFESS_TALK_MSG("GroupAllowConfessTalkEvent", Constant.NONE),
    // 允许群员邀请好友加群
    GROUP_ALLOW_MUMBER_INVITE_MSG("GroupAllowMemberInviteEvent", Constant.NONE),
    // 新人入群事件
    MEMBER_JOIN_MSG("MemberJoinEvent", MemberJoinMsgHandler.class.getName()),
    // 非bot成员被踢出群
    MEMBER_LEAVE_KICK_MSG("MemberLeaveEventKick", Constant.NONE),
    // 非bot成员主动离群
    MEMBER_LEAVE_QUIT_MSG("MemberLeaveEventQuit", Constant.NONE),
    // 群名片改动
    MEMBER_CARD_CHANGE_MSG("MemberCardChangeEvent", Constant.NONE),
    // 群头衔改动（只有群主有操作限权）
    MEMBER_SPECIAL_TITLE_CHANGE_MSG("MemberSpecialTitleChangeEvent", Constant.NONE),
    // 成员权限改变的事件（该成员不是Bot）
    MEMBER_PERMISSION_CHANGE_MSG("MemberPermissionChangeEvent", Constant.NONE),
    // 群成员被禁言事件（该成员不是Bot）
    MEMBER_MUTE_MSG("MemberMuteEvent", Constant.NONE),
    // 群成员被取消禁言事件（该成员不是Bot）
    MEMBER_UNMUTE_MSG("MemberUnmuteEvent", Constant.NONE),
    // 群员称号改变
    MEMBER_HONOR_CHANGE_MSG("MemberHonorChangeEvent", Constant.NONE),


    // 申请事件
    // 添加好友申请
    NEW_FRIEND_REQUESTS_MSG("NewFriendRequestEvent", NewFriendRequestsMsgHandler.class.getName()),
    // 用户入群申请（Bot需要有管理员权限）
    MEMBER_JOIN_REQUESTS_MSG("MemberJoinRequestEvent", Constant.NONE),
    // Bot被邀请入群申请
    BOT_INVITED_JOIN_GROUP_REQUESTS_MSG("BotInvitedJoinGroupRequestEvent", BotInvitedJoinGroupRequestsMsgHandler.class.getName()),


    // 其他客户端事件
    // 其他客户端上线
    OTHER_CLIENT_ONLINE_MSG("OtherClientOnlineEvent", Constant.NONE),
    // 其他客户端下线
    OTHER_CLIENT_OFFLINE_MSG("OtherClientOfflineEvent", Constant.NONE),


    // 命令事件
    // 命令被执行
    COMMAND_EXECUTED_MSG("CommandExecutedEvent", Constant.NONE);

    private String eventName;
    private String className;

    MsgCallBackEnum(String eventName, String className) {
        this.eventName = eventName;
        this.className = className;
    }

    public String getEventName() {
        return eventName;
    }

    public String getClassName() {
        return className;
    }

    public static String getClassNameByEvent(String eventName) {
        String className = null;
        for (MsgCallBackEnum msgCallBackStrategyEnum : MsgCallBackEnum.values()) {
            if (msgCallBackStrategyEnum.eventName.equals(eventName)) {
                className = msgCallBackStrategyEnum.getClassName();
                if (!Constant.NONE.equals(className)) {
                    break;
                }
            }
        }
        // 若用户自己有事件，扫描用户定义的
        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, BotEvent.class);
        for (Class<?> c : classSet) {
            BotEvent botEvent = c.getDeclaredAnnotation(BotEvent.class);
            if (botEvent.event().getEventName().equals(eventName)) {
                Class<?> superclass = c.getSuperclass();
                String superClassName = superclass.getName();
                String botMsgClassName = AbstractBotMsgHandler.class.getName();
                if(superClassName.equals(botMsgClassName)){
                    className = c.getName();
                }
            }
        }
        return className;
    }

}

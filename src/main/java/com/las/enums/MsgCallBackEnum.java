package com.las.enums;

import com.las.strategy.handle.*;

public enum MsgCallBackEnum {
    // 文档 ↓
    // https://docs.mirai.mamoe.net/mirai-api-http/api/EventType.html

    // 消息事件
    // 群消息
    GROUP_MSG("GroupMessage", GroupMsgHandler.class.getName()),
    // 好友消息
    FRIEND_MSG("FriendMessage", FriendMsgHandler.class.getName()),
    //临时消息
    TEMP_MSG("TempMessage", TempMsgHandler.class.getName()),
    //陌生人消息
    STRANGER_MSG("StrangerMessage", StrangerMsgHandler.class.getName()),
    //其他客户端消息
    OTHER_CLIENT_MSG("OtherClientMessage", OtherClientMsgHandler.class.getName()),


    // bot自身事件
    // bot上线
    BOT_ONLINE_MSG("BotOnlineEvent", BotOnlineMsgHandler.class.getName()),
    //bot主动离线
    BOT_OFFLINE_ACTIVE_MSG("BotOfflineEventActive", BotOfflineActiveMsgHandler.class.getName()),
    //被挤下线
    BOT_OFFLINE_FORCE_MSG("BotOfflineEventForce", BotOfflineForceMsgHandler.class.getName()),
    // bot掉线
    BOT_OFFLINE_DROPED_MSG("BotOfflineEventDropped", BotOfflineDropedMsgHandler.class.getName()),
    // bot主动重新登录
    BOT_RELOGIN_MSG("BotReloginEvent", BotReloginMsgHandler.class.getName()),


    // 好友事件
    // 好友输入状态改变
    FRIEND_INPUT_STATUS_CHANGED_MSG("FriendInputStatusChangedEvent", FriendInputStatusChangedMsgHandler.class.getName()),
    // 好友昵称改变
    FRIEND_NICK_CHANGED_MSG("FriendNickChangedEvent", FriendNickChangedMsgHandler.class.getName()),


    // 群事件
    //Bot在群里的权限被改变. 操作人一定是群主
    BOT_GROUP_PERMISSION_CHANGE_MSG("BotGroupPermissionChangeEvent", BotGroupPermissionChangeMsgHandler.class.getName()),
    // Bot被禁言
    BOT_MUTE_MSG("BotMuteEvent", BotMuteMsgHandler.class.getName()),
    // Bot被取消禁言
    BOT_UNMUTE_MSG("BotUnmuteEvent", BotUnmuteMsgHandler.class.getName()),
    // Bot加入了一个新群
    BOT_JOIN_GROUP_MSG("BotJoinGroupEvent", BotJoinGroupMsgHandler.class.getName()),
    // Bot主动退群
    BOT_LEAVE_ACTIVE_MSG("BotLeaveEventActive", BotLeaveActiveMsgHandler.class.getName()),
    // Bot被踢出一个群
    BOT_LEAVE_KICK_MSG("BotLeaveEventKick", BotLeaveKickMsgHandler.class.getName()),
    // 群消息撤回
    GROUP_RECALL_MSG("GroupRecallEvent", GroupRecallMsgHanlder.class.getName()),
    // 好友消息撤回
    FRIEND_RECALL_MSG("FriendRecallEvent", FriendRecallMsgHandler.class.getName()),
    // 戳一戳事件
    NUDGE_MSG("NudgeEvent", NudgeMsgHandler.class.getName()),
    // 某群名改变
    GROUP_NAME_CHANGE_MSG("GroupNameChangeEvent", GroupNameChangeMsgHandler.class.getName()),
    // 某群入群公告改变
    GROUP_ENTRANCE_ANNOUNCEMENT_CHANGE_MSG("GroupEntranceAnnouncementChangeEvent", GroupEntranceAnnouncementChangeMsgHandler.class.getName()),
    // 全员禁言
    GROUP_MUTE_ALL_MSG("GroupMuteAllEvent", GroupMuteAllMsgHandler.class.getName()),
    // 匿名聊天
    GROUP_ALLOW_ANONYMOUS_CHAT_MSG("GroupAllowAnonymousChatEvent", GroupAllowAnonymousChatEvent.class.getName()),
    // 坦白说
    GROUP_ALLOW_CONFESS_TALK_MSG("GroupAllowConfessTalkEvent", GroupAllowConfessTalkMsgHandler.class.getName()),
    // 允许群员邀请好友加群
    GROUP_ALLOW_MUMBER_INVITE_MSG("GroupAllowMemberInviteEvent", GroupAllowMemberInviteMsgHandler.class.getName()),
    // 新人入群事件
    MEMBER_JOIN_MSG("MemberJoinEvent", MemberJoinMsgHandler.class.getName()),
    // 非bot成员被踢出群
    MEMBER_LEAVE_KICK_MSG("MemberLeaveEventKick", MemberLeaveKickMsgHandler.class.getName()),
    // 非bot成员主动离群
    MEMBER_LEAVE_QUIT_MSG("MemberLeaveEventQuit", MemberLeaveQuitMsgHandler.class.getName()),
    // 群名片改动
    MEMBER_CARD_CHANGE_MSG("MemberCardChangeEvent", MemberCardChangeMsgHandler.class.getName()),
    // 群头衔改动（只有群主有操作限权）
    MEMBER_SPECIAL_TITLE_CHANGE_MSG("MemberSpecialTitleChangeEvent", MemberSpecialTitleChangeMsgHandler.class.getName()),
    // 成员权限改变的事件（该成员不是Bot）
    MEMBER_PERMISSION_CHANGE_MSG("MemberPermissionChangeEvent", MemberPermissionChangeMsgHandler.class.getName()),
    // 群成员被禁言事件（该成员不是Bot）
    MEMBER_MUTE_MSG("MemberMuteEvent", MemberMuteMsgHandler.class.getName()),
    // 群成员被取消禁言事件（该成员不是Bot）
    MEMBER_UNMUTE_MSG("MemberUnmuteEvent", MemberUnmuteMsgHandler.class.getName()),
    // 群员称号改变
    MEMBER_HONOR_CHANGE_MSG("MemberHonorChangeEvent", MemberHonorChangeMsgHandler.class.getName()),


    // 申请事件
    // 添加好友申请
    NEW_FRIEND_REQUESTS_MSG("NewFriendRequestEvent", NewFriendRequestsMsgHandler.class.getName()),
    // 用户入群申请（Bot需要有管理员权限）
    MEMBER_JOIN_REQUESTS_MSG("MemberJoinRequestEvent", MemberJoinRequestsMsgHandler.class.getName()),
    // Bot被邀请入群申请
    BOT_INVITED_JOIN_GROUP_REQUESTS_MSG("BotInvitedJoinGroupRequestEvent", BotInvitedJoinGroupRequestsMsgHandler.class.getName()),


    // 其他客户端事件
    // 其他客户端上线
    OTHER_CLIENT_ONLINE_MSG("OtherClientOnlineEvent", OtherClientOnlineMsgHandler.class.getName()),
    // 其他客户端下线
    OTHER_CLIENT_OFFLINE_MSG("OtherClientOfflineEvent", OtherClientOfflineMsgHandler.class.getName()),


    // 命令事件
    // 命令被执行
    COMMAND_EXECUTED_MSG("CommandExecutedEvent", CommandExecutedMsgHandler.class.getName());

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

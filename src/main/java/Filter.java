import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import commmand.Strategy;
import java.util.Objects;

public class Filter {
    public static void mainFilter(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        switch (jsonObject.getString("type")){
            //https://docs.mirai.mamoe.net/mirai-api-http/api/MessageType.html
                // 消息事件
            case "GroupMessage":
            case "FriendMessage":
            case "TempMessage":  //群临时消息
            case "StrangerMessage": //陌生人消息
            case "OtherClientMessage": //其他客户端消息
            //https://docs.mirai.mamoe.net/mirai-api-http/api/EventType.html
                // bot自身事件
            case "BotOnlineEvent":  //bot上线
            case "BotOfflineEventActive":  //bot主动离线
            case "BotOfflineEventForce":  //被挤下线
            case "BotOfflineEventDropped":  // bot掉线
            case "BotReloginEvent": // bot主动重新登录
                // 好友事件
            case "FriendInputStatusChangedEvent":  // 好友输入状态改变
            case "FriendNickChangedEvent":  // 好友昵称改变
                // 群事件
            case "BotGroupPermissionChangeEvent":  //Bot在群里的权限被改变. 操作人一定是群主
            case "BotMuteEvent":  // Bot被禁言
            case "BotUnmuteEvent":  // Bot被取消禁言
            case "BotJoinGroupEvent":  // Bot加入了一个新群
            case "BotLeaveEventActive":  // Bot主动退群
            case "BotLeaveEventKick":  // Bot被踢出一个群
            case "GroupRecallEvent":  // 群消息撤回
            case "FriendRecallEvent":  // 好友消息撤回
            case "NudgeEvent":  // 戳一戳事件
            case "GroupNameChangeEvent":  // 某群名改变
            case "GroupEntranceAnnouncementChangeEvent":  // 某群入群公告改变
            case "GroupMuteAllEvent":  // 全员禁言
            case "GroupAllowAnonymousChatEvent":  // 匿名聊天
            case "GroupAllowConfessTalkEvent":  // 坦白说
            case "GroupAllowMemberInviteEvent":  // 允许群员邀请好友加群
            case "MemberJoinEvent":  // 新人入群事件
            case "MemberLeaveEventKick":  // 非bot成员被踢出群
            case "MemberLeaveEventQuit":  // 非bot成员主动离群
            case "MemberCardChangeEvent":  // 群名片改动
            case "MemberSpecialTitleChangeEvent":  // 群头衔改动（只有群主有操作限权）
            case "MemberPermissionChangeEvent":  // 成员权限改变的事件（该成员不是Bot）
            case "MemberMuteEvent":  // 群成员被禁言事件（该成员不是Bot）
            case "MemberUnmuteEvent":  // 群成员被取消禁言事件（该成员不是Bot）
            case "MemberHonorChangeEvent":  // 群员称号改变
                // 申请事件
            case "NewFriendRequestEvent":  // 添加好友申请
            case "MemberJoinRequestEvent":  // 用户入群申请（Bot需要有管理员权限）
            case "BotInvitedJoinGroupRequestEvent":  // Bot被邀请入群申请
                // 其他客户端事件
            case "OtherClientOnlineEvent":  // 其他客户端上线
            case "OtherClientOfflineEvent":  // 其他客户端下线
                // 命令事件
            case "CommandExecutedEvent":  // 命令被执行
        }
    }

    @NotNull
    private static JSONObject filter(@NotNull JSONObject msg){
        JSONObject msgProp = new JSONObject();
        int superuser = Configs.env.superuser.qq;
        if (msg.getInteger("sender") == superuser) {
            msgProp.put("", "");
        }
        return msgProp;
    }
}

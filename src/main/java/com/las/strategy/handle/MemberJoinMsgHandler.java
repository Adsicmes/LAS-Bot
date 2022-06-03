package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.common.Constant;
import com.las.model.GroupExt;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;

public class MemberJoinMsgHandler extends BotMsgHandler {

    @Override
    public void exec() {
        JSONObject member = getMsgObject().getJSONObject("member");
        Long gId = member.getJSONObject("group").getLong("id");
        Long uId = member.getLong("id");
        GroupExt groupExt = getGroupExtDao().findByGid(gId);
        String tip = groupExt.getAttribute1();
        // 若欢迎提示不为空，则欢迎新人入群
        if (StrUtils.isNotBlank(tip)) {
            CmdUtil.sendAtMessage(tip, uId, uId, gId, Constant.MESSAGE_TYPE_GROUP);
        }
    }

}
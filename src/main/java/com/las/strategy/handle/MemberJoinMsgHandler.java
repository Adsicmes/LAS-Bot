package com.las.strategy.handle;

import com.las.common.Constant;
import com.las.model.GroupExt;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;

public class MemberJoinMsgHandler extends BotMsgHandler {

    @Override
    public void exec() {
        if (Constant.MESSAGE_TYPE_GROUP == getMsgType()) {
            GroupExt groupExt = getGroupExtDao().findByGid(getId());
            String tip = groupExt.getAttribute1();
            // 若欢迎提示不为空，则欢迎新人入群
            if (StrUtils.isNotBlank(tip)) {
                CmdUtil.sendAtMessage(tip, getUserId(), getUserId(), getId(), getMsgType());
            }
        }
    }

}
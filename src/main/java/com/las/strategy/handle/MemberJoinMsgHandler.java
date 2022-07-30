package com.las.strategy.handle;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.las.common.Constant;
import com.las.model.GroupExt;
import com.las.utils.mirai.CmdUtil;
import com.las.utils.StrUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author dullwolf
 */
@Component
@Scope("prototype")
public class MemberJoinMsgHandler extends AbstractBotMsgHandler {

    @Override
    public void exec() {
        JSONObject member = getCqObj().getJSONObject("member");
        Long gId = member.getJSONObject("group").getLong("id");
        Long uId = member.getLong("id");
        GroupExt groupExt = getGroupExtDao().findByGid(gId);
        if (ObjectUtil.isNotNull(groupExt)) {
            String tip = groupExt.getAttribute1();
            // 若欢迎提示不为空，则欢迎新人入群
            if (StrUtils.isNotBlank(tip)) {
                CmdUtil.sendAtMessage(tip, uId, uId, gId, Constant.MESSAGE_TYPE_GROUP);
            }
        }
    }

}
package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.utils.MiraiUtil;
import org.apache.log4j.Logger;

/**
 * @author dullwolf
 */
public class BotInvitedJoinGroupRequestsMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(BotInvitedJoinGroupRequestsMsgHandler.class);

    @Override
    public void exec() {
        JSONObject object = getCqObj();
        logger.info(object.toJSONString());
        //邀请进群
        MiraiUtil.getInstance().agreeGroup(object);
    }
}

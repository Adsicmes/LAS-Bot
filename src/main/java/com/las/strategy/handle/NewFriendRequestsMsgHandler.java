package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.utils.MiraiUtil;
import org.apache.log4j.Logger;

public class NewFriendRequestsMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(NewFriendRequestsMsgHandler.class);

    @Override
    public void exec() {
        JSONObject object = getMsgObject();
        logger.info(object.toJSONString());
        //添加好友
        MiraiUtil.getInstance().agreeFriend(object);
    }
}

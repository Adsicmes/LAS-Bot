package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.strategy.BotMsgHandler;
import com.las.utils.MiraiUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class BotOnlineMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(BotOnlineMsgHandler.class);

    @Override
    public void exec() {
        List<JSONObject> list = MiraiUtil.getInstance().getGroupList();
        list.forEach(item -> logger.info(item));

    }
}

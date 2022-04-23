package com.las.strategy.handle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.las.strategy.BotMsgHandler;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

public class FriendMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(FriendMsgHandler.class);

    @Override
    public void exec() {
        JSONObject sender = getSender();
        JSONArray msgChain = getMsgChain();
        logger.info("得到了sender --> " + sender.toJSONString());
        logger.info("得到了msgChain --> " + msgChain.toJSONString());
        String msgData = getMsgData();
        if (StrUtils.isNotBlank(msgData)) {
            CmdUtil.exeCommand(msgData, getUserId(), getId(), getMsgType());
        }

    }
}

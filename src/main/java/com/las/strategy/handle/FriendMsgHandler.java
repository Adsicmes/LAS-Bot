package com.las.strategy.handle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.las.model.GroupFun;
import com.las.strategy.BotMsgHandler;
import org.apache.log4j.Logger;

import java.util.List;

public class FriendMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(FriendMsgHandler.class);

    @Override
    public void exec() {
        JSONObject sender = getSender();
        JSONArray msgChain = getMsgChain();
        logger.info("得到了sender --> " + sender.toJSONString());
        logger.info("得到了msgChain --> " + msgChain.toJSONString());

        List<GroupFun> groupFunList = getGroupFunDao().queryGroup(1483492332L);
        groupFunList.forEach(groupFun -> logger.info(JSONObject.toJSONString(groupFun)));

    }
}

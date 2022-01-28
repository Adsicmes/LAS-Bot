package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.strategy.AbstractMsgHandler;
import org.apache.log4j.Logger;

public class FriendMsgHandler extends AbstractMsgHandler {

    private static Logger logger = Logger.getLogger(FriendMsgHandler.class);

    @Override
    public void exec(){
        JSONObject sender = getSender();
        logger.info("获得sender --> " + sender.toJSONString());

    }

}

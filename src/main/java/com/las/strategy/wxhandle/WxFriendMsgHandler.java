package com.las.strategy.wxhandle;

import com.las.strategy.handle.BotMsgHandler;
import org.apache.log4j.Logger;

public class WxFriendMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(WxFriendMsgHandler.class);

    @Override
    public void exec() {
        logger.info("处理一条微信好友消息...");
    }
}

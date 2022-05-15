package com.las.strategy.handle;

import org.apache.log4j.Logger;

public class FriendMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(FriendMsgHandler.class);

    @Override
    public void exec() {
        String msgData = getMsgData();
        exeCommand(msgData, getUserId(), getId(), getMsgType());
    }
}

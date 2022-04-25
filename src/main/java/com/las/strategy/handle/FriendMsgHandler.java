package com.las.strategy.handle;

import com.las.strategy.BotMsgHandler;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

public class FriendMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(FriendMsgHandler.class);

    @Override
    public void exec() {
        String msgData = getMsgData();
        if (StrUtils.isNotBlank(msgData)) {
            exeCommand(msgData, getUserId(), getId(), getMsgType());
        }

    }
}

package com.las.strategy.handle;

import com.las.strategy.BotMsgHandler;
import org.apache.log4j.Logger;

public class BotOnlineMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(BotOnlineMsgHandler.class);

    @Override
    public void exec() {
        initBot();
    }
}

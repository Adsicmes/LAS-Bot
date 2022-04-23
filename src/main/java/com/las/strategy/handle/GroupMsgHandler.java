package com.las.strategy.handle;

import com.las.strategy.BotMsgHandler;
import org.apache.log4j.Logger;

public class GroupMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(GroupMsgHandler.class);

    @Override
    public void exec() {
        //执行指令
        exeCommand();
    }


}

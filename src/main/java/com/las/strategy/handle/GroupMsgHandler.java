package com.las.strategy.handle;

import com.las.strategy.BotMsgHandler;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

public class GroupMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(GroupMsgHandler.class);

    @Override
    public void exec() {
        String msgData = getMsgData();
        if(StrUtils.isNotBlank(msgData) && msgData.startsWith("#")){
            logger.info("匹配到群消息前缀是#符号，开始执行指令");
            //执行指令
            exeCommand();
        }
    }


}

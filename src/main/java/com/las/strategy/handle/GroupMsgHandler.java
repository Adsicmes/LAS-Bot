package com.las.strategy.handle;

import org.apache.log4j.Logger;

public class GroupMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(GroupMsgHandler.class);

    @Override
    public void exec() {
        String msgData = getMsgData();
        //直接执行指令
        exeCommand(msgData, getUserId(), getId(), getMsgType());

        //后续有一些拉黑功能逻辑,以及记录用户使用指令使用情况，消息插入数据库等等，后面再慢慢完善
    }


}

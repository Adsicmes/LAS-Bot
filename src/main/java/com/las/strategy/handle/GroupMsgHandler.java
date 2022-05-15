package com.las.strategy.handle;

import com.las.common.Constant;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

public class GroupMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(GroupMsgHandler.class);

    @Override
    public void exec() {
        String msgData = getMsgData();
        //直接执行指令
        exeCommand(msgData, getUserId(), getId(), getMsgType());

        //后续有一些拉黑功能逻辑在慢慢完善
    }


}

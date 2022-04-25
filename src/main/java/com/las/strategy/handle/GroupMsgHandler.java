package com.las.strategy.handle;

import com.las.common.Constant;
import com.las.strategy.BotMsgHandler;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

public class GroupMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(GroupMsgHandler.class);

    @Override
    public void exec() {
        String msgData = getMsgData();
        if (StrUtils.isNotBlank(msgData)) {
            if (msgData.startsWith(Constant.DEFAULT_PRE)) {
                logger.info("匹配到群消息前缀是#符号，开始执行指令");
                //执行指令
                exeCommand(msgData, getUserId(), getId(), getMsgType());
            } else {
                //直接执行指令（后续可以弄数据库配置决定）
                exeCommand(msgData, getUserId(), getId(), getMsgType());
            }
        }
    }


}

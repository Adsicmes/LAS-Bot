package com.las.event;

import com.las.annotation.BotEvent;
import com.las.enums.MsgCallBackEnum;
import com.las.strategy.handle.AbstractBotMsgHandler;
import org.apache.log4j.Logger;

/**
 * 自定义QQ私聊消息事件
 * @author dullwolf
 */
@BotEvent(event = MsgCallBackEnum.FRIEND_MSG)
public class MyEvent extends AbstractBotMsgHandler {

    private static Logger logger = Logger.getLogger(MyEvent.class);

    @Override
    public void exec() {
        logger.info("进入到自定义QQ事件");

    }

}

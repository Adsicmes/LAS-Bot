package com.las.event;

import com.las.strategy.handle.AbstractBotMsgHandler;
import org.apache.log4j.Logger;

/**
 * 自定义QQ私聊消息事件（此类作为参考）
 * @author dullwolf
 */
//@BotEvent(event = MsgCallBackEnum.FRIEND_MSG)
public class MyEvent extends AbstractBotMsgHandler {

    private static Logger logger = Logger.getLogger(MyEvent.class);

    @Override
    public void exec() {
        logger.info("进入到自定义QQ事件");
        // 操作DAO
        // UserDao userDao = getUserDao();

        // 消息内容
        String msgData = getMsgData();
        // 组ID
        Long id = getId();
        // 用户ID
        Long userId = getUserId();
        // 消息类型
        int type = getType();

        // 触发指令
        exeCommand(msgData,userId,id,type);

        // 甚至可以自己其他自定义的事情等...
        // 最后总结：一定要记得带@BotEvent注解，不带则走内置机器人的默认事件

    }

}

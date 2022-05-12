package com.las.strategy.wxhandle;

import com.las.strategy.BotStrategy;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 该抽象类的作用就是实现各种处理
 */
public abstract class WxBotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(WxBotMsgHandler.class);



    /**
     * 实现接口的执行消息方法(子类可以去重新实现)
     */
    @Override
    public void exec() {
        logger.info("bot开始执行默认消息...");
        //此方法可以由子类重写方法去做对应的事件
    }

    /**
     * 实现接口的处理消息方法(子类不可以去重新实现)
     */
    @Override
    public final void handleMsg(Map map) {

    }




}
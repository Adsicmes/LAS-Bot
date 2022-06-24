package com.las.strategy.wxhandle;

import com.las.strategy.BotStrategy;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author dullwolf
 */
public abstract class AbstractWxBotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(AbstractWxBotMsgHandler.class);


    /**
     * 实现接口的处理消息方法(子类不可以去重新实现)
     */
    @Override
    public final void handleMsg(Map map) {

    }




}
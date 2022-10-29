package com.las.strategy.wxhandle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.las.strategy.BotStrategy;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author dullwolf
 */
public abstract class AbstractWxBotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(AbstractWxBotMsgHandler.class);

    /**
     * 消息完整对象
     */
    private JSONObject msgData;


    /**
     * 提供getter
     */
    protected JSONObject getMsgData() {
        return msgData;
    }


    /**
     * 实现接口的处理消息方法(子类不可以去重新实现)
     */
    @Override
    public final void handleMsg(Map map) {
        msgData = JSON.parseObject(JSONObject.toJSONString(map));

    }






}
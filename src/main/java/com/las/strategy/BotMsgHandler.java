package com.las.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 该抽象类的作用就是实现各种处理
 *
 */
public abstract class BotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(BotMsgHandler.class);

    private JSONObject sender;

    private JSONArray msgChain;

    //后续安装下lombok插件，就不用总是写getter方法了，很累...
    public JSONObject getSender() {
        return sender;
    }

    public JSONArray getMsgChain() {
        return msgChain;
    }

    /**
     * 实现接口的处理消息方法(不可以让子类去实现)
     */
    @Override
    public final void handleMsg(Map map) {
        logger.info("bot开始处理消息...");
        sender = handleSender(JSON.parseObject(JSONObject.toJSONString(map)));
        msgChain = handleMsgChain(JSON.parseObject(JSONObject.toJSONString(map)));
    }

    /**
     * 定义处理sender消息的方法
     *
     */
    private JSONObject handleSender(JSONObject jsonObject) {
        return jsonObject.getJSONObject("sender");
    }

    /**
     * 定义处理messageChain消息的方法
     */
    private JSONArray handleMsgChain(JSONObject jsonObject){
        return jsonObject.getJSONArray("messageChain");
    }

}
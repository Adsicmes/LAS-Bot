package com.las.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.las.dao.GroupDao;
import com.las.dao.UserDao;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.las.config.AppConfigs.APP_CONTEXT;

/**
 * 该抽象类的作用就是实现各种处理
 */
public abstract class BotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(BotMsgHandler.class);

    private JSONObject sender;

    private JSONArray msgChain;

    private GroupDao groupDao;

    private UserDao userDao;

    public BotMsgHandler() {
        this.groupDao = (GroupDao) APP_CONTEXT.getBean("groupDao");
        this.userDao = (UserDao) APP_CONTEXT.getBean("userDao");
    }

    //后续安装下lombok插件，就不用总是写getter方法了，很累...
    public JSONObject getSender() {
        return sender;
    }

    public JSONArray getMsgChain() {
        return msgChain;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * 实现接口的执行消息方法(子类也可以去重新实现)
     */
    @Override
    public void exec() {
        logger.info("bot开始执行默认消息...");
        //此方法可以由子类重写方法去做对应的事件
    }

    /**
     * 实现接口的处理消息方法(子类也可以去重新实现)
     */
    @Override
    public void handleMsg(Map map) {
        logger.info("bot开始处理消息...");
        JSONObject object = JSON.parseObject(JSONObject.toJSONString(map));
        sender = handleSender(object);
        msgChain = handleMsgChain(object);
    }

    /**
     * 定义处理sender消息的方法
     */
    private JSONObject handleSender(JSONObject jsonObject) {
        return jsonObject.getJSONObject("sender");
    }

    /**
     * 定义处理messageChain消息的方法
     */
    private JSONArray handleMsgChain(JSONObject jsonObject) {
        return jsonObject.getJSONArray("messageChain");
    }

}
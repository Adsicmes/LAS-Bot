package com.las.cmd;

import com.alibaba.fastjson.JSONObject;
import com.las.strategy.wxhandle.AbstractWxBotMsgHandler;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
public abstract class BaseWxCommand extends AbstractWxBotMsgHandler {


    @Override
    public void exec() {

    }


    /**
     * 执行非匹配指令（所有参数用上）
     *
     * @param msgObj  完整消息对象
     * @param wxId   封装WXID信息
     * @param command 指令 ( 包含指令参数,多个参数中间空格隔开的 )
     */
    public abstract void execute(JSONObject msgObj, String wxId, String command) throws Exception;
}

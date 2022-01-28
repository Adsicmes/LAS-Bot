package com.las.strategy.handle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.las.strategy.AbstractMsgHandler;

public class GroupMsgHandler extends AbstractMsgHandler {


    @Override
    public void exec() {
        JSONArray msgChain = getMsgChain();
        JSONObject sender = getSender();
    }
}

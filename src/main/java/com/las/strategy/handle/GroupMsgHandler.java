package com.las.strategy.handle;

public class GroupMsgHandler extends AbstractBotMsgHandler {

    @Override
    public void exec() {
        //直接执行指令
        exeCommand(getMsgData(), getUserId(), getId(), getType());
    }


}

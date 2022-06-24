package com.las.strategy.handle;

public class FriendMsgHandler extends AbstractBotMsgHandler {

    @Override
    public void exec() {
        exeCommand(getMsgData(), getUserId(), getId(), getType());
    }

}

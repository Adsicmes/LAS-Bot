package com.las.strategy.handle;

import org.springframework.stereotype.Component;

@Component
public class FriendMsgHandler extends AbstractBotMsgHandler {

    @Override
    public void exec() {
        exeCommand(getMsgData(), getUserId(), getId(), getType());
    }

}

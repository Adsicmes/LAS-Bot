package com.las.strategy.handle;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class FriendMsgHandler extends AbstractBotMsgHandler {

    @Override
    public void exec() {
        exeCommand(getMsgData(), getUserId(), getId(), getType());
    }

}

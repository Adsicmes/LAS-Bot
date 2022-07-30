package com.las.strategy.handle;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GroupMsgHandler extends AbstractBotMsgHandler {

    @Override
    public void exec() {
        //直接执行指令
        exeCommand(getMsgData(), getUserId(), getId(), getType());
    }


}

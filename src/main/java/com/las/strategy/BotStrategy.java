package com.las.strategy;

import java.util.Map;

public interface BotStrategy {

    /**
     * 处理map消息
     *
     */
    void handleMsg(Map map);

    /**
     * 执行消息
     */
    void exec();


//    /**
//     * 执行指令方法
//     */
//    void exeCommand(String msg, Long userId, Long id, int type);
}

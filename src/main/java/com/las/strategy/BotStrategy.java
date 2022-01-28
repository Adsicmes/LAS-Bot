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
}

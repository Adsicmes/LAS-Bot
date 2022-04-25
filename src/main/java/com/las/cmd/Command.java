package com.las.cmd;


import com.las.strategy.handle.BotMsgHandler;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command extends BotMsgHandler {

    private String name;                // 指令名
    private ArrayList<String> alias;    // 其他指向这个指令的指令名

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public void setAlias(ArrayList<String> alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", alias=" + alias +
                '}';
    }

    /**
     * 指令构造器
     *
     * @param name       指令名
     * @param alias      其他指令名
     */
    public Command(String name, String... alias) {
        this(name, new ArrayList<>(Arrays.asList(alias)));
    }


    /**
     * 指令构造器
     *
     * @param name       指令名
     * @param alias      其他指令名
     */
    private Command(String name, ArrayList<String> alias) {
        this.name = name;
        this.alias = alias;
    }



    /**
     * 执行指令
     *
     * @param userId  封装用户ID信息
     * @param id      封装的ID（用户ID or 群ID or 讨论组ID）
     * @param type    封装消息类型
     * @param command 指令 ( 包含指令参数,多个参数中间空格隔开的 )
     * @param args    指令参数
     */
    public abstract void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args);
}
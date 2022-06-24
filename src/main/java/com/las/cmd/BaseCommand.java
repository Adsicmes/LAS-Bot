package com.las.cmd;


import com.alibaba.fastjson.JSONObject;
import com.las.strategy.handle.AbstractBotMsgHandler;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author dullwolf
 */
public abstract class BaseCommand extends AbstractBotMsgHandler {

    /**
     * 指令名
     */
    private String name;

    /**
     * 其他指向这个指令的指令名
     */
    private ArrayList<String> alias;

    /**
     * 优先级
     */
    private int priority;

    public String getName() {
        return name;
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * 指令构造器
     *
     * @param name  指令名
     * @param alias 其他指令名
     */
    public BaseCommand(String name, String... alias) {
        this(0, name, new ArrayList<>(Arrays.asList(alias)));
    }

    /**
     * 指令构造器
     *
     * @param name  指令名
     * @param alias 其他指令名
     */
    public BaseCommand(int priority, String name, String... alias) {
        this(priority, name, new ArrayList<>(Arrays.asList(alias)));
    }

    private BaseCommand(int priority, String name, ArrayList<String> alias) {
        this.priority = priority;
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
    public abstract void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception;

    /**
     * 执行非匹配指令默认方法（子类BaseNonCommand继承）
     */
    public void execute(JSONObject msgObj, Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {

    }

    @Override
    public void exec() {

    }

    @Override
    public String toString() {
        return "BaseCommand{" +
                "name='" + name + '\'' +
                ", alias=" + alias +
                ", priority=" + priority +
                '}';
    }
}
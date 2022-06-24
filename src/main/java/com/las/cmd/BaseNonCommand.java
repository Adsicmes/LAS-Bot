package com.las.cmd;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
public abstract class BaseNonCommand extends BaseCommand {

    public BaseNonCommand() {
        super("非匹配指令","NotCommand");
    }

    public BaseNonCommand(String name, String... alias) {
        super(name, alias);
    }

    @Override
    public final void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args){

    }

    /**
     * 执行非匹配指令（所有参数用上）
     *
     * @param msgObj  完整消息对象
     * @param userId  封装用户ID信息
     * @param id      封装的ID（用户ID or 群ID or 讨论组ID）
     * @param type    封装消息类型
     * @param command 指令 ( 包含指令参数,多个参数中间空格隔开的 )
     * @param args    指令参数
     * @throws Exception
     */
    @Override
    public abstract void execute(JSONObject msgObj, Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception;
}

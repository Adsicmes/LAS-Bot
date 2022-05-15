package com.las.cmd;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public abstract class NotCommand extends Command {

    public NotCommand() {
        super("非匹配指令","NotCommand");
    }

    public NotCommand(String name, String... alias) {
        super(name, alias);
    }

    @Override
    public final void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {

    }

    @Override
    public abstract void execute(JSONObject msgObj, Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception;
}

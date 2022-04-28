package com.las.cmd.admin;

import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.config.AppConfigs;
import com.las.utils.CmdUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;

@BotCmd(funName = "超管功能", funWeight = 999)
public class ResetFun extends Command {

    private static Logger logger = Logger.getLogger(ResetFun.class);


    public ResetFun() {
        super("权限更新", "funUp");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (null != userId) {
            long superQQ = Long.parseLong(AppConfigs.SUPER_QQ);
            if (userId != superQQ) {
                CmdUtil.sendMessage("必须是超管才可以更新机器人权限", userId, id, type);
            } else {
                initBotFun();
                CmdUtil.sendMessage("更新成功", userId, id, type);
            }
        } else {
            // 可能是系统内部执行更新，不需要发送CQ
            logger.warn("正准备初始化机器人,请稍后...");
            initBotFun();
        }
    }
}
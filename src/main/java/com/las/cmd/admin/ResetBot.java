package com.las.cmd.admin;

import com.las.annotation.BotCmd;
import com.las.cmd.BaseCommand;
import com.las.config.AppConfigs;
import com.las.utils.CmdUtil;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
@BotCmd(funName = "超管功能", funWeight = 999)
public class ResetBot extends BaseCommand {

    public ResetBot() {
        super("重置", "reset");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (!userId.equals(Long.parseLong(AppConfigs.SUPER_QQ))) {
            CmdUtil.sendMessage("必须是超管才可以重置机器人", userId, id, type);
        } else {
            initBot();
            CmdUtil.sendMessage("重置成功", userId, id, type);
        }
    }
}

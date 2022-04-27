package com.las.cmd.admin;

import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.config.AppConfigs;
import com.las.utils.CmdUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;

@BotCmd
public class ResetBot extends Command {

    private static Logger logger = Logger.getLogger(ResetBot.class);


    public ResetBot() {
        super("重置", "reset");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        long superQQ = Long.parseLong(AppConfigs.SUPER_QQ);
        if (userId != superQQ) {
            CmdUtil.sendMessage("必须是超管才可以重置机器人", userId, id, type);
        } else {
            initBot();
            CmdUtil.sendMessage("重置成功", userId, id, type);
        }
    }
}

package com.las.cmd.judge;

import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import org.apache.log4j.Logger;

import java.util.ArrayList;

@BotCmd(funName = "鉴定色图", funWeight = 0, isMatch = false)
public class ImgllegalCmd extends Command {

    private static Logger logger = Logger.getLogger(ImgllegalCmd.class);

    public ImgllegalCmd() {
        super("鉴定色图", "");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        logger.info("鉴定色图开始...");

    }
}

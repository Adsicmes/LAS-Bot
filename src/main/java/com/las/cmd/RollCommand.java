package com.las.cmd;


import com.las.annotation.BotCmd;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

@BotCmd
public class RollCommand extends Command {

    private static Logger logger = Logger.getLogger(RollCommand.class);


    public RollCommand() {
        super("roll", "摇一摇");
    }


    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        int min = 1, max = 100;
        if (args.size() == 1) {
            if (StrUtils.isNumeric(String.valueOf(args.get(0)))) {
                max = Integer.parseInt(args.get(0));
            }
        }
        if (args.size() > 1) {
            if (StrUtils.isNumeric(String.valueOf(args.get(0))) &&
                    StrUtils.isNumeric(String.valueOf(args.get(1)))) {
                min = Integer.parseInt(args.get(0));
                max = Integer.parseInt(args.get(1));
            }
        }
        int randNumber = new Random().nextInt(max - min + 1) + min;
        CmdUtil.sendMessage(String.format("摇到了:%d", randNumber), userId, id, type);
    }
}

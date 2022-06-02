package com.las.cmd.group;

import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.model.GroupExt;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;

import java.util.ArrayList;

@BotCmd(funName = "管理员功能", funWeight = 996)
public class GroupJoinTip extends Command {

    public GroupJoinTip() {
        super("群欢迎设置", "groupTip");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (args.size() == 2) {
            // 必须只传两个参数，例如 群欢迎设置 群号 欢迎提示(带“空”字表示取消)
            String gId = args.get(0);
            String tip = args.get(1);
            if (StrUtils.isNumeric(gId) && StrUtils.isNotEmpty(tip)) {
                if ("空".equals(tip) || "None".equalsIgnoreCase(tip)) {
                    tip = "";
                }
                GroupExt groupExt = getGroupExtDao().findByGid(Long.parseLong(gId));
                if (null != groupExt) {
                    groupExt.setAttribute1(tip);
                    getGroupExtDao().saveOrUpdate(groupExt);
                    CmdUtil.sendMessage("更新成功", userId, id, type);
                }
            }

        }
    }
}

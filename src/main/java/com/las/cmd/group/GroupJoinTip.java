package com.las.cmd.group;

import com.las.annotation.BotCmd;
import com.las.cmd.BaseCommand;
import com.las.common.Constant;
import com.las.model.GroupExt;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
@BotCmd(funName = "管理员功能", funWeight = 996)
public class GroupJoinTip extends BaseCommand {

    public GroupJoinTip() {
        super(Constant.MIN_PRIORITY,"群欢迎设置", "groupTip","gt");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (Constant.MESSAGE_TYPE_GROUP == type) {
            // 必须是在群里使用，例如 群欢迎设置 欢迎提示(带“空”字表示取消)
            String tip = null;
            if (args.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : args) {
                    sb.append(s).append(" ");
                }
                tip = sb.toString();
            }
            if (StrUtils.isNotEmpty(tip)) {
                if (Constant.KONG.equals(tip.trim()) || Constant.NONE.equalsIgnoreCase(tip.trim())) {
                    tip = "";
                }
                GroupExt groupExt = getGroupExtDao().findByGid(id);
                if (null != groupExt) {
                    groupExt.setAttribute1(tip);
                    getGroupExtDao().saveOrUpdate(groupExt);
                    CmdUtil.sendMessage("更新成功", userId, id, type);
                }
            }

        }
    }
}

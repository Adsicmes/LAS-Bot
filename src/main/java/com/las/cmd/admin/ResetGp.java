package com.las.cmd.admin;

import cn.hutool.core.collection.CollectionUtil;
import com.las.annotation.BotCmd;
import com.las.cmd.BaseCommand;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.model.GroupExt;
import com.las.utils.CmdUtil;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
@BotCmd(funName = "超管功能", funWeight = 999)
public class ResetGp extends BaseCommand {

    public ResetGp() {
        super("群设置", "GP");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (!userId.equals(Long.parseLong(AppConfigs.SUPER_QQ))) {
            CmdUtil.sendMessage("必须是超管才可以设置群配置", userId, id, type);
        } else {
            if (Constant.MESSAGE_TYPE_GROUP == type) {
                if (CollectionUtil.isNotEmpty(args)) {
                    // 获得前缀
                    String prefix = args.get(0).trim();
                    if (Constant.KONG.equals(prefix) || Constant.NONE.equalsIgnoreCase(prefix)) {
                        prefix = "";
                    }
                    GroupExt groupExt = getGroupExtDao().findByGid(id);
                    if (null != groupExt) {
                        groupExt.setAttribute2(prefix);
                    }
                    getGroupExtDao().saveOrUpdate(groupExt);
                    CmdUtil.sendMessage("更新成功", userId, id, type);
                }
            }
        }
    }
}

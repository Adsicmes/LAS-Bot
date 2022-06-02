package com.las.cmd.admin;


import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.config.AppConfigs;
import com.las.model.User;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

@BotCmd(funName = "超管功能", funWeight = 999)
public class ResetWeight extends Command {

    private static Logger logger = Logger.getLogger(ResetFun.class);


    public ResetWeight() {
        super("权限赋能", "WeightUp");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (null != userId) {
            long superQQ = Long.parseLong(AppConfigs.SUPER_QQ);
            if (userId != superQQ) {
                CmdUtil.sendMessage("必须是超管才可以更新机器人权限", userId, id, type);
            } else {
                // 给用户设置权限 1~995
                // 996、997、998 分别是普通管理员、中级管理员、高级管理员
                if (args.size() == 2) {
                    // 必须只传两个参数，例如 权限赋能 用户QQ号 权限值
                    String qq = args.get(0);
                    String weight = args.get(1);
                    if (StrUtils.isNumeric(qq) && StrUtils.isNumeric(weight)) {
                        User qqUser = getUserDao().findByUid(Long.parseLong(qq));
                        if (null == qqUser) {
                            // 数据库不存在，直接创建
                            qqUser = new User();
                            qqUser.setRemark("初次初始化权限赋能");
                        }
                        qqUser.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
                        qqUser.setUserId(Long.parseLong(qq));
                        qqUser.setFunPermission(Integer.parseInt(weight));
                        getUserDao().saveOrUpdate(qqUser);
                    }
                    CmdUtil.sendMessage("更新成功", userId, id, type);
                }
            }
        }
    }
}
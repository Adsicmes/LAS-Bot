package com.las.cmd.admin;


import com.las.annotation.BotCmd;
import com.las.cmd.BaseCommand;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.model.User;
import com.las.utils.CmdUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
@BotCmd(funName = "超管功能", funWeight = 999)
public class ResetWeight extends BaseCommand {

    private static Logger logger = Logger.getLogger(ResetFun.class);


    public ResetWeight() {
        super("权限赋能", "权限更新");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        if (null != userId) {
            if (!userId.equals(Long.parseLong(AppConfigs.superQQ))) {
                CmdUtil.sendMessage("必须是超管才可以更新机器人权限", userId, id, type);
            } else if (Constant.MESSAGE_TYPE_GROUP == type) {
                // 必须是在群里使用
                if (args.size() == 2) {
                    // 必须只传两个参数，例如 权限赋能 用户QQ号 权限值
                    String qq = args.get(0);
                    String weight = args.get(1);
                    if (StrUtils.isNumeric(qq) && StrUtils.isNumeric(weight)) {
                        User qqUser = null;
                        if (command.startsWith(Constant.PERMISSION_ENABLE) && Integer.parseInt(weight) > 995) {
                            // 权限赋能 996、997、998 分别是普通管理员、中级管理员、高级管理员
                            qqUser = getUserDao().findGroupUser(Long.parseLong(qq), id);
                            if (null == qqUser) {
                                // 数据库不存在，直接创建并且一定要设置备注
                                qqUser = new User();
                                // 备注后面还需要带群ID
                                qqUser.setRemark(Constant.INIT_PERMISSION + id);
                            }
                        } else if (command.startsWith(Constant.PERMISSION_UPDATE) && Integer.parseInt(weight) < 996) {
                            // 权限更新 是给用户设置权限 1~995
                            qqUser = getUserDao().findByUid(Long.parseLong(qq));
                            if (null == qqUser) {
                                qqUser = new User();
                            }
                        }
                        if (null != qqUser) {
                            qqUser.setBotQQ(Long.parseLong(AppConfigs.botQQ));
                            qqUser.setUserId(Long.parseLong(qq));
                            qqUser.setFunPermission(Integer.parseInt(weight));
                            getUserDao().saveOrUpdate(qqUser);
                            CmdUtil.sendMessage("更新成功", userId, id, type);
                        }
                    }
                }
            }
        }
    }
}
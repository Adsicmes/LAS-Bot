package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.User;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    /**
     * 查找超管QQ信息
     *
     * @return User
     */
    public User findSuperQQ() throws Exception {
        String sql = "select * from `user` where user_id = ? and bot_qq = ?";
        return getRunner().query(sql, new BeanHandler<>(User.class, getProcessor()), Long.parseLong(AppConfigs.SUPER_QQ), Long.parseLong(AppConfigs.BOT_QQ));
    }


    /**
     * 根据用户QQ查找一条记录
     *
     * ps：排除初次初始化权限赋能的，因为群管理员和普通用户用同一个权限字段 fun_weight，
     * 只能通过remark区分，大部分企业公司都是用这招的！！
     *
     * @param uid 用户QQ
     * @return User
     */
    public User findByUid(Long uid) {
        String sql = "select * from `user` where user_id = ? and bot_qq = ? and remark <> '初次初始化权限赋能'";
        User user = null;
        try {
            user = getRunner().query(sql, new BeanHandler<>(User.class, getProcessor()), uid, Long.parseLong(AppConfigs.BOT_QQ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     *
     * 查找用户是否是群管理员，权限有996、997、998
     *
     * @param uid 用户QQ
     * @return User
     */
    public User findGroupUser(Long uid) {
        String sql = "select * from `user` where user_id = ? and bot_qq = ? and remark = '初次初始化权限赋能'";
        User user = null;
        try {
            user = getRunner().query(sql, new BeanHandler<>(User.class, getProcessor()), uid, Long.parseLong(AppConfigs.BOT_QQ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}

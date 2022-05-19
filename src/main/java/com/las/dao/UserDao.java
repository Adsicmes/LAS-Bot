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
     * @param uid 用户QQ
     * @return User
     */
    public User findByUid(Long uid) {
        String sql = "select * from `user` where user_id = ? and bot_qq = ?";
        User user = null;
        try {
            user = getRunner().query(sql, new BeanHandler<>(User.class, getProcessor()), uid, Long.parseLong(AppConfigs.BOT_QQ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}

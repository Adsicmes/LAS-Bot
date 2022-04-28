package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.Group;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GroupDao extends BaseDao<Group> {

    public GroupDao() {
        super(Group.class);
    }

    /**
     * 根据群号查找一条记录
     *
     * @param gid 群号ID
     * @return Group
     */
    public Group findByGid(Long gid) {
        String sql = "select * from `group` where group_id = ?";
        Group group = null;
        try {
            group = getRunner().query(sql, new BeanHandler<>(Group.class, getProcessor()), gid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return group;
    }


    public List<Group> findAll() {
        String sql = "select * from `group` where bot_qq = ?";
        List<Group> groupList = new ArrayList<>();
        try {
            groupList = getRunner().query(sql, new BeanListHandler<>(Group.class, getProcessor()), AppConfigs.BOT_QQ);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupList;
    }


}

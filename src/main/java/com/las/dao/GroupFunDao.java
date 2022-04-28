package com.las.dao;

import com.las.dao.base.BaseDao;
import com.las.model.Group;
import com.las.model.GroupFun;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GroupFunDao extends BaseDao<GroupFun> {

    public GroupFunDao() {
        super(GroupFun.class);
    }


    public List<GroupFun> findListByGid(Long gid) {
        String sql = "select * from `group_fun` where is_enable = 1 and group_id = ?";
        List<GroupFun> groupFunList = new ArrayList<>();
        try {
            groupFunList = getRunner().query(sql, new BeanListHandler<>(GroupFun.class, getProcessor()), gid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupFunList;
    }


}

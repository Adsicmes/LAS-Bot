package com.las.dao;

import com.las.dao.base.BaseDao;
import com.las.model.GroupFun;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupFunDao extends BaseDao<GroupFun> {

    public GroupFunDao() {
        super(GroupFun.class);
    }

    /**
     * 根据群号查找对应权限的功能
     *
     */
    public List<GroupFun> queryGroup(long qqGroup) {
        String sql = "select functions from qqGroups where qqGroup = ?";
        return findList(sql, qqGroup);
    }

}

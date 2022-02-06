package com.las.dao;

import com.las.dao.base.BaseDao;
import com.las.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupDao extends BaseDao<Group> {

    public GroupDao() {
        super(Group.class);
    }


}

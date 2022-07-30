package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.GroupFun;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dullwolf
 */
@Component
public class GroupFunDao extends BaseDao<GroupFun> {

    private static Logger logger = Logger.getLogger(GroupFunDao.class);

    public GroupFunDao() {
        super(GroupFun.class);
    }


    public List<GroupFun> findListByGid(Long gid) {
        String sql = "select * from `group_fun` where group_id = ? and bot_qq = ?";
        List<GroupFun> groupFunList = new ArrayList<>();
        try {
            groupFunList = getRunner().query(sql, new BeanListHandler<>(GroupFun.class, getProcessor()), gid, Long.parseLong(AppConfigs.botQQ));
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return groupFunList;
    }

    public boolean deleteById(Long id) {
        String sql = "delete from `group_fun` where id = ? and bot_qq = ?";
        int row = 0;
        try {
            row = getRunner().update(sql, id, AppConfigs.botQQ);
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return row > 0;
    }


}

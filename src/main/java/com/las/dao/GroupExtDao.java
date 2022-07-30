package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.GroupExt;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @author dullwolf
 */
@Component
public class GroupExtDao extends BaseDao<GroupExt> {

    private static Logger logger = Logger.getLogger(GroupExtDao.class);

    public GroupExtDao() {
        super(GroupExt.class);
    }

    /**
     * 根据群号查找一条记录
     *
     * @param gid 群号ID
     * @return GroupExt
     */
    public GroupExt findByGid(Long gid) {
        String sql = "select * from `group_ext` where group_id = ? and bot_qq = ?";
        GroupExt groupExt = null;
        try {
            groupExt = getRunner().query(sql, new BeanHandler<>(GroupExt.class, getProcessor()), gid, Long.parseLong(AppConfigs.botQQ));
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return groupExt;
    }


}

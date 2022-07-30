package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.Fun;
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
public class FunDao extends BaseDao<Fun> {

    private static Logger logger = Logger.getLogger(FunDao.class);

    public FunDao() {
        super(Fun.class);
    }

    /**
     * 查找当前机器人QQ下所有的指令功能
     */
    public List<Fun> findAll() {
        String sql = "select * from `fun` where bot_qq = ?";
        List<Fun> funList = new ArrayList<>();
        try {
            funList = getRunner().query(sql, new BeanListHandler<>(Fun.class, getProcessor()), AppConfigs.botQQ);
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return funList;
    }

    public boolean deleteAll() {
        String sql = "delete from `fun` where bot_qq = ?";
        int row = 0;
        try {
            row = getRunner().update(sql, AppConfigs.botQQ);
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return row > 0;
    }




}

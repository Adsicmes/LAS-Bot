package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.Fun;
import com.las.service.qqbot.netty.adapter.BotServerHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dullwolf
 */
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
            funList = getRunner().query(sql, new BeanListHandler<>(Fun.class, getProcessor()), AppConfigs.BOT_QQ);
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return funList;
    }


}

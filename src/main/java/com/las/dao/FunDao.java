package com.las.dao;

import com.las.config.AppConfigs;
import com.las.dao.base.BaseDao;
import com.las.model.Fun;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FunDao extends BaseDao<Fun> {

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
            e.printStackTrace();
        }
        return funList;
    }


}

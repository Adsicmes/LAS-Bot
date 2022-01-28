package com.las.dao.base;

import com.las.config.AppConfigs;
import com.las.model.GroupFun;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDao<T> {

    private Class<T> aClass;

    public BaseDao(Class<T> aClass) {
        this.aClass = aClass;
    }

    private QueryRunner getRunner() {
        return new QueryRunner(AppConfigs.DATA_SOURCE);
    }

    protected List<T> findList(String sql, Object... params) {
        List<T> list = new ArrayList<>();
        try {
            list = getRunner().query(sql, new BeanListHandler<>(aClass), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}

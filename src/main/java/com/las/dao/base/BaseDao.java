package com.las.dao.base;

import com.las.annotation.Column;
import com.las.annotation.Table;
import com.las.config.AppConfigs;
import com.las.strategy.BotMsgHandler;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDao<T> {

    private static Logger logger = Logger.getLogger(BaseDao.class);

    private Class<T> aClass;
    private String tableName;
    private String idKey;
    //开启驼峰映射
    private RowProcessor processor = new BasicRowProcessor(new GenerousBeanProcessor());

    public BaseDao(Class<T> aClass) {
        this.aClass = aClass;
        if (aClass.isAnnotationPresent(Table.class)) {
            tableName = aClass.getDeclaredAnnotation(Table.class).value();
            idKey = aClass.getDeclaredAnnotation(Table.class).idKey();
        }
    }

    private QueryRunner getRunner() {
        return new QueryRunner(AppConfigs.DATA_SOURCE);
    }

    public T findById(Object id) {
        String sql = "select * from " + tableName + " where " + idKey + " = ?";
        T bean = null;
        try {
            bean = getRunner().query(sql, new BeanHandler<>(aClass, processor), id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean saveOrUpdate(T obj) {
        StringBuilder sql = new StringBuilder();
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<Object> params = new ArrayList<>();
        Object id;
        boolean isUpdate;
        int row = 0;
        try {
            id = checkIdExist(obj, fields);
            isUpdate = null != id;
            if (isUpdate) {
                sql.append(" update ").append(tableName).append(" set ");
            } else {
                sql.append(" insert into ").append(tableName).append(" ( ");
            }
            for (Field field : fields) {
                String colName = field.getName();
                if (field.isAnnotationPresent(Column.class)) {
                    colName = field.getDeclaredAnnotation(Column.class).value();
                }
                field.setAccessible(true);
                Object value = field.get(obj);
                if (null != value) {
                    params.add(value);
                    if (isUpdate) {
                        sql.append(colName).append(" = ?,");
                    } else {
                        sql.append(colName).append(",");
                    }
                }
            }
            sql.delete(sql.lastIndexOf(","), sql.length());
            // 后面还有拼接逻辑
            if (isUpdate) {
                sql.append(" where ").append(idKey).append(" = ? ");
                params.add(id);
            } else {
                sql.append(" ) values ( ");
                for (int i = 0; i < params.size(); i++) {
                    sql.append(" ?, ");
                }
                sql.delete(sql.lastIndexOf(","), sql.length());
                sql.append(")");
            }
            //打印拼接的sql是？
            logger.debug("sql：" + sql.toString());
            row = getRunner().update(sql.toString(), params.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    /**
     * 检查ID是否在数据库存在
     */
    private Object checkIdExist(T obj, Field[] fields) throws Exception {
        Object id = null;
        for (Field field : fields) {
            String colName = field.getName();
            if (field.isAnnotationPresent(Column.class)) {
                colName = field.getDeclaredAnnotation(Column.class).value();
            }
            if (colName.equals(idKey)) {
                field.setAccessible(true);
                id = field.get(obj);
                break;
            }
        }
        if (null != id && null != findById(id)) {
            return id;
        }
        return null;
    }


}

package com.las.dao.base;

import com.las.annotation.Column;
import com.las.annotation.Table;
import com.las.config.AppConfigs;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dullwolf
 */
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
            tableName = "`" + aClass.getDeclaredAnnotation(Table.class).value() + "`";
            idKey = aClass.getDeclaredAnnotation(Table.class).idKey();
        }
    }

    public QueryRunner getRunner() {
        return new QueryRunner(AppConfigs.DATA_SOURCE);
    }

    public RowProcessor getProcessor() {
        return processor;
    }

    /**
     * 根据ID查找
     * @param id 数据库主键ID
     */
    public T findById(Object id) {
        String sql = "select * from " + tableName + " where " + idKey + " = ?";
        T bean = null;
        try {
            bean = getRunner().query(sql, new BeanHandler<>(aClass, processor), id);
        } catch (SQLException e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return bean;
    }

    /**
     * 更新或者插入
     *
     */
    public void saveOrUpdate(T obj) {
        StringBuilder sql = new StringBuilder();
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<Object> params = new ArrayList<>();
        Object id;
        boolean isUpdate;
        try {
            id = checkIdExist(obj, fields);
            isUpdate = null != id;
            if (isUpdate) {
                sql.append(" update ").append(tableName).append(" set ");
            } else {
                sql.append(" insert into ").append(tableName).append("(").append(idKey).append(",");
                params.add(null);
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
                sql.append(") values (");
                for (int i = 0; i < params.size(); i++) {
                    sql.append("?,");
                }
                sql.delete(sql.lastIndexOf(","), sql.length());
                sql.append(")");
            }
            //打印拼接的sql是？
            logger.debug("sql：" + sql.toString());
            getRunner().update(sql.toString(), params.toArray());
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
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

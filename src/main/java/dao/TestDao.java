package dao;

import config.AppConfigs;

import java.sql.*;

public class TestDao {

    private static Connection conn = null;
    private static Statement stmt = null;

    public static void sign(long qqGroup, String function) {
        try {
            // 注册JDBC驱动
            Class.forName(AppConfigs.DRIVER);
            // 建立连接
            conn = DriverManager.getConnection(AppConfigs.JDBC, AppConfigs.USER, AppConfigs.PWD);
            stmt = conn.createStatement();

            String sql;
            sql = String.format("insert into qqGroups (qqGroup, functions) VALUES (%d, '%s');", qqGroup, function);

            stmt.executeUpdate(sql);  //执行

        } catch (Exception se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static String query(long qqGroup) {
        try {
            // 注册JDBC驱动
            Class.forName(AppConfigs.DRIVER);
            // 建立连接
            conn = DriverManager.getConnection(AppConfigs.JDBC, AppConfigs.USER, AppConfigs.PWD);
            stmt = conn.createStatement();

            String sql;
            sql = String.format("select functions from qqGroups where qqGroup = %d;;", qqGroup);
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();
            return resultSet.getString(1);

        } catch (Exception se) {
            // 处理 JDBC 错误
            se.printStackTrace();
            return null;
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

}

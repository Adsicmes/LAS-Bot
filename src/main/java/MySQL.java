import java.sql.*;

public class MySQL {
    static final String JDBC_DRIVER = Configs.env.MySql.driver;
    // 数据库的路径，用户名与密码，需要根据自己的设置
    static final String DB_URL = Configs.env.MySql.jdbc; // = "jdbc:mysql://localhost:3306/fff";
    static final String USER = Configs.env.MySql.user;
    static final String PASSWD = Configs.env.MySql.passwd;

    public static class qqGroupFunctions {
        public static void main(String[] args) {
            //sign(1483492332, "sdfw");
            System.out.println(query(1483492332));
        }
        static Connection conn = null;
        static Statement stmt = null;
        public static void sign(int qqGroup, String function){
            try {
                // 注册JDBC驱动
                Class.forName(JDBC_DRIVER);
                // 建立连接
                conn = DriverManager.getConnection(DB_URL, USER, PASSWD);
                stmt = conn.createStatement();

                String sql;
                sql = String.format("insert into qqGroups (qqGroup, functions) VALUES (%d, '%s');", qqGroup, function);

                stmt.executeUpdate(sql);  //执行

            } catch(Exception se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }// 处理 Class.forName 错误
            finally{
                // 关闭资源
                try{
                    if(stmt!=null) stmt.close();
                }catch(SQLException ignored){
                }// 什么都不做
                try{
                    if(conn!=null) conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        }
        public static String query(int qqGroup){
            try {
                // 注册JDBC驱动
                Class.forName(JDBC_DRIVER);
                // 建立连接
                conn = DriverManager.getConnection(DB_URL, USER, PASSWD);
                stmt = conn.createStatement();

                String sql;
                sql = String.format("select functions from qqGroups where qqGroup = %d;;", qqGroup);
                ResultSet resultSet= stmt.executeQuery(sql);
                resultSet.next();
                return resultSet.getString(1);

            } catch(Exception se){
                // 处理 JDBC 错误
                se.printStackTrace();
                return null;
            }// 处理 Class.forName 错误
            finally{
                // 关闭资源
                try{
                    if(stmt!=null) stmt.close();
                }catch(SQLException ignored){
                }// 什么都不做
                try{
                    if(conn!=null) conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        }
    }
}
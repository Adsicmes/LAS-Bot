package com.las.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.las.dao.UserDao;
import com.las.model.User;
import org.apache.log4j.Logger;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;


public class AppConfigs {

    private static Logger logger = Logger.getLogger(AppConfigs.class);

    //常量名字为全大写
    public static String SUPER_QQ;
    public static String BOT_QQ;
    public static String KEY_AUTH;
    public static String MIRAT_API_URL;
    public static String QQ_BOT_SERVER;
    public static String WEB_PATH;
    public static ClassPathXmlApplicationContext APP_CONTEXT;
    public static DruidDataSource DATA_SOURCE;


    static {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        IniSection iniSection;
        //设置mysql数据账号密码
        iniSection = getInit(path).getSection("dbmysql");
        String DRIVER = iniSection.getItem("driver").getValue();
        String JDBC = iniSection.getItem("jdbc").getValue();
        String USER = iniSection.getItem("user").getValue();
        String PWD = iniSection.getItem("passwd").getValue();
        logger.debug("数据库连接DRIVER信息：" + DRIVER);
        DATA_SOURCE = new DruidDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(JDBC);
        DATA_SOURCE.setUsername(USER);
        DATA_SOURCE.setPassword(PWD);

        //获取botQQ
        iniSection = getInit(path).getSection("botqq");
        BOT_QQ = iniSection.getItem("qq").getValue();
        KEY_AUTH = iniSection.getItem("qqAuth").getValue();
        MIRAT_API_URL = iniSection.getItem("miraiUrl").getValue();
        QQ_BOT_SERVER = iniSection.getItem("botServer").getValue();
        logger.info("botQQ是：" + BOT_QQ);
        iniSection = getInit(path).getSection("webpath");
        WEB_PATH = iniSection.getItem("webpath").getValue();

        //超级管理员
        iniSection = getInit(path).getSection("superuser");
        SUPER_QQ = iniSection.getItem("superqq").getValue();

        // 根据路径地址构建文件
        File html = new File(System.getProperty("user.dir"), WEB_PATH);
        logger.debug("根据路径地址构建文件信息：" + html.getAbsolutePath());

        //最后一步，初始化spring容器
        APP_CONTEXT = new ClassPathXmlApplicationContext("spring-context.xml");
        UserDao userDao = (UserDao) APP_CONTEXT.getBean("userDao");
        User superUser = null;
        try {
            superUser = userDao.findSuperQQ();
            if (null != superUser) {
                logger.debug("检查管理员QQ信息：" + superUser.toString());
            } else {
                logger.warn("该机器人QQ未添加管理员好友");
            }
        } catch (Exception e) {
            logger.error("数据库连接异常，请检查配置");
        }

    }

    /**
     * ini文件需要每次都初始化读取配置
     *
     * @param path 文件路径
     * @return IniFile对象
     */
    private static IniFile getInit(String path) {
        IniFile iniFile = new BasicIniFile();
        IniFileReader red = new IniFileReader(iniFile, new File(path));
        try {
            red.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iniFile;
    }


}

package com.las.config;

import com.alibaba.druid.pool.DruidDataSource;
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
    public static final String QQ;
    public static final String QQ_AUTH;
    public static final String MIRAT_API_URL;
    public static final String QQ_BOT_SERVER;
    public static final String WEB_PATH;
    public static final ClassPathXmlApplicationContext APP_CONTEXT;
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
        QQ = iniSection.getItem("qq").getValue();
        QQ_AUTH = iniSection.getItem("qqAuth").getValue();
        MIRAT_API_URL = iniSection.getItem("miraiUrl").getValue();
        QQ_BOT_SERVER = iniSection.getItem("botServer").getValue();
        logger.info("botQQ是：" + QQ);
        iniSection = getInit(path).getSection("webpath");
        WEB_PATH = iniSection.getItem("webpath").getValue();

        //最后一步，初始化spring容器
        APP_CONTEXT = new ClassPathXmlApplicationContext("spring-context.xml");

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

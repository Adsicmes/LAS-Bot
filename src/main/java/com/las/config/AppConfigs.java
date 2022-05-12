package com.las.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.las.cmd.admin.ResetFun;
import com.las.service.wx.WeChatPushService;
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

    //微信机器人常量
    public static String WX_SERVER_URL;
    public static WeChatPushService WX_PUSH_SERVER;


    static {
        String envPath = System.getProperty("user.dir") + File.separator + "env.ini";
        IniSection envIniSection;
        //设置mysql数据账号密码
        envIniSection = getInit(envPath).getSection("dbmysql");
        String DRIVER = envIniSection.getItem("driver").getValue();
        String JDBC = envIniSection.getItem("jdbc").getValue();
        String USER = envIniSection.getItem("user").getValue();
        String PWD = envIniSection.getItem("passwd").getValue();
        logger.debug("数据库连接DRIVER信息：" + DRIVER);
        DATA_SOURCE = new DruidDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(JDBC);
        DATA_SOURCE.setUsername(USER);
        DATA_SOURCE.setPassword(PWD);


        // 分开两个配置，上面的是数据库，下面的是bot配置

        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        IniSection iniSection;
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

        //微信机器服务
        iniSection = getInit(path).getSection("wxserver");
        WX_SERVER_URL = iniSection.getItem("wxserverurl").getValue();

        // 根据路径地址构建文件
        File html = new File(System.getProperty("user.dir"), WEB_PATH);
        logger.debug("根据路径地址构建文件信息：" + html.getAbsolutePath());

        //最后一步，初始化spring容器
        APP_CONTEXT = new ClassPathXmlApplicationContext("spring-context.xml");

        // 更新机器人权限
        try {
            new ResetFun().execute(null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化机器人权限失败，原因：" + e.getMessage());
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

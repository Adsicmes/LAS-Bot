package com.las.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.las.cmd.admin.ResetFun;
import com.las.core.Bot;
import com.las.service.wx.WeChatPushService;
import org.apache.log4j.Logger;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;

/**
 * @author dullwolf
 */
public class AppConfigs {

    private static Logger logger = Logger.getLogger(AppConfigs.class);

    public static String superQQ;
    public static String botQQ;
    public static String keyAuth;
    public static String miRaiApiUrl;
    public static String qqBotServer;
    public static String webPath;
    public static DruidDataSource dataSource;
    public static String wxServerUrl;
    public static WeChatPushService wxPushService;
    public static ClassPathXmlApplicationContext context;


    static {
        // 分开两个配置，数据库环境初始化、bot配置初始化
        initEnv();
        initBot();
    }

    private static void initBot() {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        IniSection iniSection;
        // 超级管理员
        iniSection = getInit(path).getSection("superuser");
        superQQ = iniSection.getItem("superqq").getValue();
        // 获取botQQ
        iniSection = getInit(path).getSection("botqq");
        botQQ = iniSection.getItem("qq").getValue();
        keyAuth = iniSection.getItem("qqAuth").getValue();
        miRaiApiUrl = iniSection.getItem("miraiUrl").getValue();
        qqBotServer = iniSection.getItem("botServer").getValue();
        logger.info("botQQ是：" + botQQ);
        iniSection = getInit(path).getSection("webpath");
        webPath = iniSection.getItem("webpath").getValue();

        // 微信机器服务
        iniSection = getInit(path).getSection("wxserver");
        wxServerUrl = iniSection.getItem("wxserverurl").getValue();
        // 根据路径地址构建文件
        File html = new File(System.getProperty("user.dir"), webPath);
        logger.debug("根据路径地址构建文件信息：" + html.getAbsolutePath());

        // 初始spring容器
        context = new ClassPathXmlApplicationContext("spring.xml");

        // 更新机器人权限
        try {
            new ResetFun().execute(null, null, null, null, null);
        } catch (Exception e) {
            logger.error("初始化机器人权限失败，原因：" + e.getMessage(), e);
        }
    }

    private static void initEnv() {
        String envPath = System.getProperty("user.dir") + File.separator + "env.ini";
        IniSection envIniSection;
        //设置mysql数据账号密码
        envIniSection = getInit(envPath).getSection("dbmysql");
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(envIniSection.getItem("driver").getValue());
        dataSource.setUrl(envIniSection.getItem("jdbc").getValue());
        dataSource.setUsername(envIniSection.getItem("user").getValue());
        dataSource.setPassword(envIniSection.getItem("passwd").getValue());
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
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return iniFile;
    }


}

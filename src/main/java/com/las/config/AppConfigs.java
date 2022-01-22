package com.las.config;

import org.apache.log4j.Logger;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class AppConfigs {

    private static Logger logger = Logger.getLogger(AppConfigs.class);

    //常量名字为全大写
    public static final int QQ;
    public static final String DRIVER;
    public static final String JDBC;
    public static final String USER;
    public static final String PWD;

    static {
        URL url = ClassLoader.getSystemResource("env.ini");
        String path = url.toString().replaceAll("file:/", "");
        logger.info("当前env配置路径是：" + path);

        IniSection iniSection;
        //获取管理员QQ
        iniSection = getInit(path).getSection("superuser");
        QQ = Integer.parseInt(iniSection.getItem("qq").getValue());
        logger.info("管理员QQ是：" + QQ);

        //设置mysql数据账号密码
        iniSection = getInit(path).getSection("dbmysql");
        DRIVER = iniSection.getItem("driver").getValue();
        JDBC = iniSection.getItem("jdbc").getValue();
        USER = iniSection.getItem("user").getValue();
        PWD = iniSection.getItem("passwd").getValue();
        logger.info("数据库连接DRIVER信息：" + DRIVER);
        logger.info("数据库连接JDBC信息：" + JDBC);
        logger.info("数据库连接USER信息：" + USER);
        logger.info("数据库连接PWD信息：" + PWD);
    }

    /**
     * ini文件需要每次都初始化读取配置
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

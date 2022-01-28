package com.las.config;

import org.apache.log4j.Logger;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import java.io.*;


public class AppConfigs {

    private static Logger logger = Logger.getLogger(AppConfigs.class);

    //常量名字为全大写
    public static final int QQ;
    public static final String DRIVER;
    public static final String JDBC;
    public static final String USER;
    public static final String PWD;

    static {
        try {
            InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("env.ini");
            BufferedReader br = new BufferedReader(new InputStreamReader(initialStream));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bot.ini")));
            String line;
            while (null != (line = br.readLine())){
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            br.close();
            initialStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        logger.debug("当前env配置路径是：" + path);

//        URL url = ClassLoader.getSystemResource("env.ini");
//        String path = url.getPath();
//        logger.info("当前env配置路径是：" + path);

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
        logger.debug("数据库连接DRIVER信息：" + DRIVER);
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

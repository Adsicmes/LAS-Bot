package com.las.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.las.annotation.EnableMirai;
import com.las.cmd.admin.ResetFun;
import com.las.service.wx.WeChatPushService;
import com.las.utils.ClassUtil;
import org.apache.log4j.Logger;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Set;

/**
 * @author dullwolf
 */
@Configuration
@ComponentScan(
        basePackages = {"com.las"}
)
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


    static {
        // 分开两个配置，数据库环境初始化、bot配置初始化
        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, EnableMirai.class);
        for (Class<?> aClass : classSet) {
            EnableMirai annotation = aClass.getDeclaredAnnotation(EnableMirai.class);
            if (annotation != null) {
                // 初始化环境
                initEnv(annotation);
                initBot(annotation);
                break;
            }
        }
    }

    private static void initBot(EnableMirai annotation) {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        IniSection iniSection;
        // 超级管理员
        iniSection = getInit(path,annotation).getSection("superuser");
        superQQ = iniSection.getItem("superqq").getValue();
        // 获取botQQ
        iniSection = getInit(path,annotation).getSection("botqq");
        botQQ = iniSection.getItem("qq").getValue();
        keyAuth = iniSection.getItem("qqAuth").getValue();
        miRaiApiUrl = iniSection.getItem("miraiUrl").getValue();
        qqBotServer = iniSection.getItem("botServer").getValue();
        logger.info("botQQ是：" + botQQ);
        iniSection = getInit(path,annotation).getSection("webpath");
        webPath = iniSection.getItem("webpath").getValue();

        // 微信机器服务
        iniSection = getInit(path,annotation).getSection("wxserver");
        wxServerUrl = iniSection.getItem("wxserverurl").getValue();
        // 根据路径地址构建文件
        File html = new File(System.getProperty("user.dir"), webPath);
        logger.debug("根据路径地址构建文件信息：" + html.getAbsolutePath());

    }

    private static void initEnv(EnableMirai annotation) {
        String envPath = System.getProperty("user.dir") + File.separator + "env.ini";
        IniSection envIniSection;
        //设置mysql数据账号密码
        envIniSection = getInit(envPath,annotation).getSection("dbmysql");
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
    private static IniFile getInit(String path,EnableMirai annotation) {
        readEnvFile();
        readBotFile(annotation);
        IniFile iniFile = new BasicIniFile();
        IniFileReader red = new IniFileReader(iniFile, new File(path));
        try {
            red.read();
        } catch (IOException e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return iniFile;
    }

    private static void readEnvFile() {
        String path = System.getProperty("user.dir") + File.separator + "env.ini";
        logger.debug("当前env配置路径是：" + path);
        InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("env.ini");
        BufferedReader br;
        BufferedWriter bw;
        try {
            br = new BufferedReader(new InputStreamReader(initialStream));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("env.ini")));
            String line;
            while (null != (line = br.readLine())) {
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            br.close();
            initialStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void readBotFile(EnableMirai annotation) {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        logger.debug("当前bot配置路径是：" + path);
        InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("qqbot.ini");
        BufferedReader br;
        BufferedWriter bw;
        try {
            br = new BufferedReader(new InputStreamReader(initialStream));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bot.ini")));
            String line;
            while (null != (line = br.readLine())) {
                bw.write(changeLine(line, annotation));
                bw.newLine();
                bw.flush();
            }
            bw.close();
            br.close();
            initialStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更改文件中的替换文字
     */
    private static String changeLine(String content, EnableMirai enableMirai) {
        content = content.replaceAll("SUPER_QQ_PARAM", enableMirai.superQQ());
        content = content.replaceAll("BOT_QQ_PARAM", enableMirai.botQQ());
        content = content.replaceAll("QQ_AUTH_PARAM", enableMirai.keyAuth());
        content = content.replaceAll("MIRAI_URL_PARAM", enableMirai.miRaiUrl());
        content = content.replaceAll("BOT_SERVER_PARAM", enableMirai.botServer());
        content = content.replaceAll("WEB_PATH_PARAM", enableMirai.webPath());
        content = content.replaceAll("WX_SERVER_PARAM", enableMirai.wxServerUrl());
        return content;
    }


}

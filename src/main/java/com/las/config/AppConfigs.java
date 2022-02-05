package com.las.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.log4j.Logger;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;


public class AppConfigs {

    private static Logger logger = Logger.getLogger(AppConfigs.class);

    //常量名字为全大写
    public static final int QQ;
    public static final String WEB_PATH;
    private static final String DRIVER;
    private static final String JDBC;
    private static final String USER;
    private static final String PWD;
    public static final DruidDataSource DATA_SOURCE;
    public static final ClassPathXmlApplicationContext APP_CONTEXT;


    static {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        logger.debug("当前env配置路径是：" + path);
        try {
            InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("env.ini");
            BufferedReader br;
            BufferedWriter bw;
            //先判断是否存在
            File file = new File(path);
            if(!file.exists()){
                br = new BufferedReader(new InputStreamReader(initialStream));
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bot.ini"),"GBK"));
                String line;
                while (null != (line = br.readLine())) {
                    logger.debug("读到内容：" + line);
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                }
                bw.close();
                br.close();
                initialStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        DATA_SOURCE = new DruidDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(JDBC);
        DATA_SOURCE.setUsername(USER);
        DATA_SOURCE.setPassword(PWD);
        iniSection = getInit(path).getSection("webpath");
        WEB_PATH = iniSection.getItem("webpath").getValue();
        //最后一步，初始化spring容器
        APP_CONTEXT = new ClassPathXmlApplicationContext("spring-context.xml");
        // 下面是测试
        //GroupFunDao groupFunDao = (GroupFunDao) APP_CONTEXT.getBean("groupFunDao");
        //List<GroupFun> groupFunList = groupFunDao.queryGroup(1483492332L);
        //groupFunList.forEach(groupFun -> logger.info(JSONObject.toJSONString(groupFun)));

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

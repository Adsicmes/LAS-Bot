package com.las;

import cn.hutool.core.util.StrUtil;
import com.las.annotation.BotRun;
import com.las.config.AppConfigs;
import com.las.dao.UserDao;
import com.las.model.User;
import com.las.netty.HttpServer;
import com.las.utils.ClassUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Set;

import static com.las.config.AppConfigs.APP_CONTEXT;


public class LASBot {

    private static Logger logger = Logger.getLogger(LASBot.class);

    public static void run(Class<?> appClass) {
        BotRun botRun = appClass.getDeclaredAnnotation(BotRun.class);
        if (botRun != null) {
            String className = appClass.getName();
            int index = className.lastIndexOf(".");
            if (-1 != index) {
                String pack = className.substring(0, index);
                run(pack);
            } else {
                run("");
            }
        } else {
            run("");
        }
    }

    private static void run(String basePackage) {
        try {
            if (StrUtil.isBlank(basePackage)) {
                throw new Exception("包名路径找不到，请检查是否带了BotRun注解，项目至少在com.xxx包路径下");
            }
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(basePackage, false, BotRun.class);
            for (Class<?> aClass : classSet) {
                BotRun annotation = aClass.getDeclaredAnnotation(BotRun.class);
                if (annotation != null) {
                    // 初始化环境
                    init(annotation);
                    logger.warn("启动完成，请勿关闭程序窗口");
                    // 启动netty
                    new HttpServer(annotation.botPort()).start();
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("初始化bot失败,原因：" + e.getMessage());
        }
    }

    /**
     * 初始化配置文件
     */
    private static void init(BotRun annotation) throws Exception {
        readEnvFile();
        readBotFile(annotation);
        UserDao userDao = (UserDao) APP_CONTEXT.getBean("userDao");
        User superUser;
        try {
            superUser = userDao.findSuperQQ();
            if (null != superUser) {
                logger.debug("检查管理员QQ信息：" + superUser.toString());
            } else {
                logger.warn("机器人QQ未添加超管，请重置");
                logger.warn("请使用超管QQ(" + AppConfigs.SUPER_QQ + "),向机器人QQ(" + AppConfigs.BOT_QQ+") 发送指令<重置>");
            }
        } catch (Exception e) {
            throw new Exception("数据库连接异常，请检查env.ini配置文件");
        }
        if (!StrUtils.isNotBlank(AppConfigs.BOT_QQ)) {
            throw new Exception("botQQ暂未初始化，请检查BotRun注解里面的参数");
        }
    }

    private static void readEnvFile() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "env.ini";
        logger.debug("当前env配置路径是：" + path);
        InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("env.ini");
        BufferedReader br;
        BufferedWriter bw;
        //先判断是否存在
        File file = new File(path);
        if (!file.exists()) {
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
        }
    }


    private static void readBotFile(BotRun annotation) throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        logger.debug("当前bot配置路径是：" + path);
        InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("bot.ini");
        BufferedReader br;
        BufferedWriter bw;
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
    }

    /**
     * 更改文件中的替换文字
     */
    private static String changeLine(String content, BotRun botRun) {
        content = content.replaceAll("SUPER_QQ_PARAM", botRun.superQQ());
        content = content.replaceAll("BOT_QQ_PARAM", botRun.botQQ());
        content = content.replaceAll("QQ_AUTH_PARAM", botRun.keyAuth());
        content = content.replaceAll("MIRAI_URL_PARAM", botRun.miraiUrl());
        content = content.replaceAll("BOT_SERVER_PARAM", botRun.botServer());
        content = content.replaceAll("WEB_PATH_PARAM", botRun.webpath());
        return content;
    }

}

package com.las;

import cn.hutool.core.util.StrUtil;
import com.las.annotation.BotRun;
import com.las.config.AppConfigs;
import com.las.netty.HttpServer;
import com.las.utils.ClassUtil;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Set;


public class LASBot {

    private static Logger logger = Logger.getLogger(App.class);


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
                throw new Exception("包名路径找不到，请检查是否带了BotRun注解");
            }
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(basePackage, false, BotRun.class);
            for (Class<?> aClass : classSet) {
                BotRun annotation = aClass.getDeclaredAnnotation(BotRun.class);
                if (annotation != null) {
                    // 初始化环境
                    init(annotation);
                    // 启动netty
                    new HttpServer(annotation.botPort()).start();
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("初始化bot失败,原因：" + e.toString());
        }
    }

    /**
     * 初始化配置文件
     */
    private static void init(BotRun annotation) throws Exception {
        String path = System.getProperty("user.dir") + File.separator + "bot.ini";
        logger.debug("当前env配置路径是：" + path);
        try {
            InputStream initialStream = ClassLoader.getSystemClassLoader().getResourceAsStream("env.ini");
            BufferedReader br;
            BufferedWriter bw;
            br = new BufferedReader(new InputStreamReader(initialStream));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bot.ini"), "GBK"));
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
            e.printStackTrace();
        }
        if (StrUtils.isNotEmpty(AppConfigs.QQ)) {
            logger.info("准备初始化bot,QQ是：" + AppConfigs.QQ);
        } else {
            logger.error("botQQ暂未初始化，请检查BotRun注解里面的参数");
            throw new Exception("botQQ暂未初始化");
        }
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

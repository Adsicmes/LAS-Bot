package com.las.core;

import cn.hutool.core.util.StrUtil;
import com.las.annotation.EnableMirai;
import com.las.config.AppConfigs;
import com.las.dao.UserDao;
import com.las.model.User;
import com.las.service.qqbot.netty.HttpServer;
import com.las.service.wx.WeChatPushService;
import com.las.utils.ClassUtil;
import com.las.utils.SpringUtils;
import com.las.utils.StrUtils;
import com.las.utils.ThreadPoolUtil;
import org.apache.log4j.Logger;
import org.java_websocket.enums.ReadyState;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author dullwolf
 */
public class Bot {

    private static Logger logger = Logger.getLogger(Bot.class);

    public static void run(Class<?> appClass) {
        EnableMirai enableMirai = appClass.getDeclaredAnnotation(EnableMirai.class);
        if (enableMirai != null) {
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
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(basePackage, false, EnableMirai.class);
            for (Class<?> aClass : classSet) {
                EnableMirai annotation = aClass.getDeclaredAnnotation(EnableMirai.class);
                if (annotation != null) {
                    // 再检查环境
                    check();
                    logger.warn("启动完成，请勿关闭程序窗口");
                    ThreadPoolExecutor executor = ThreadPoolUtil.getPool();
                    executor.execute(() -> initBotService(annotation));
                    if (annotation.isEnableWxBot()) {
                        executor.execute(Bot::initWxBotService);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("初始化bot失败,原因：" + e.getMessage());
        }
    }

    /**
     * 启动QQ机器人服务
     *
     * @param annotation 注解参数
     */
    private static void initBotService(EnableMirai annotation) {
        HttpServer httpServer = new HttpServer(annotation.botPort());
        try {
            httpServer.start();
        } catch (Exception e) {
            logger.warn("启动QQ机器人失败！原因：" + e.getMessage());
        }
    }


    /**
     * 启动WX机器人服务
     */
    private static void initWxBotService() {
        while (true) {
            try {
                Thread.sleep(2000);
                WeChatPushService client;
                if (null == AppConfigs.wxPushService) {
                    // 启动WX服务
                    client = new WeChatPushService(AppConfigs.wxServerUrl);
                    client.connect();
                    while (!client.getReadyState().equals(ReadyState.OPEN)) {
                        Thread.sleep(500);
                        logger.debug("正在连接微信机器人服务...");
                    }
                    logger.warn("启动WX机器人成功");
                    AppConfigs.wxPushService = client;
                } else {
                    client = AppConfigs.wxPushService;
                    // 启动成功一次之后，若微信客户端挂了，需要不断监听连接状态
                    // 若不是OPEN，需要重新连接
                    if (!client.getReadyState().equals(ReadyState.OPEN)) {
                        client.reconnect();
                        logger.debug("正在重新连接微信机器人服务...");
                        Thread.sleep(2000);
                    }
                }
            } catch (Exception e) {
                logger.warn("启动WX机器人失败！原因：" + e.getMessage());
            }
        }
    }

    /**
     * 初始化配置文件
     */
    private static void check() throws Exception {
        UserDao userDao = SpringUtils.getBean("userDao");
        User superUser;
        try {
            superUser = userDao.findSuperQQ();
            if (null != superUser) {
                logger.debug("检查管理员QQ信息：" + superUser.toString());
            } else {
                logger.warn("机器人QQ未添加超管，请重置");
                logger.warn("请使用超管QQ(" + AppConfigs.superQQ + "),向机器人QQ(" + AppConfigs.botQQ + ") 发送指令<重置>");
            }
        } catch (Exception e) {
            throw new Exception("数据库连接异常，请检查env.ini配置文件");
        }
        if (!StrUtils.isNotBlank(AppConfigs.botQQ)) {
            throw new Exception("botQQ暂未初始化，请检查BotRun注解里面的参数");
        }
    }

}

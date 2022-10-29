package com.las.strategy.wxhandle;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.annotation.WxCmd;
import com.las.cmd.BaseCommand;
import com.las.cmd.BaseWxCommand;
import com.las.common.Constant;
import com.las.strategy.BotStrategy;
import com.las.utils.ClassUtil;
import com.las.utils.SpringUtils;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author dullwolf
 */
public abstract class AbstractWxBotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(AbstractWxBotMsgHandler.class);

    /**
     * 消息完整对象
     */
    private JSONObject msgData;


    /**
     * 提供getter
     */
    protected JSONObject getMsgData() {
        return msgData;
    }


    /**
     * 实现接口的处理消息方法(子类不可以去重新实现)
     */
    @Override
    public final void handleMsg(Map map) {
        msgData = JSON.parseObject(JSONObject.toJSONString(map));

    }

    /**
     * 执行指令方法
     */
    protected void exeCommand(String wxId, String msg) {
        try {
            String cmd = getLowerParams(msg);
            // 需要查找非匹配指令的（优先非匹配指令的）
            Set<Class<?>> notCmdSet = ClassUtil.scanPackageByAnnotation("com", false, WxCmd.class);
            for (Class<?> aClass : notCmdSet) {
                WxCmd annotation = aClass.getDeclaredAnnotation(WxCmd.class);
                if (null != annotation && !annotation.isMatch()) {
                    try {
                        String simpleName = aClass.getSimpleName();
                        String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                        Object obj = SpringUtils.getBean(beanName);
                        BaseWxCommand notCommand = (BaseWxCommand) obj;
                        logger.info("执行非匹配指令是：" + notCommand.toString());
                        notCommand.execute(getMsgData(), wxId, cmd);
                    } catch (Exception e) {
                        logger.error(super.toString() + "执行时报错，非指令命令内容:" + cmd);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("WX消息处理命令时报错：" + e.toString(), e);
        }


    }


    /**
     * 参数中多个空格转换为一个空格（并且改为小写）
     *
     * @param msg 参数消息
     * @return 优化好的参数
     */
    private String getLowerParams(String msg) {
        if (StrUtils.isBlank(msg)) {
            return null;
        }
        Matcher m = Constant.PATTERN_ONE_SPACE.matcher(msg);
        return m.replaceAll(" ").toLowerCase().trim();
    }


}
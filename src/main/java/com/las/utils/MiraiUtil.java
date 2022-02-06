package com.las.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiraiUtil {

    private static Logger logger = Logger.getLogger(MiraiUtil.class);

    private static String baseURL = AppConfigs.MIRAT_API_URL;
    private static String qq = AppConfigs.QQ;
    private static String qqAuth = AppConfigs.QQ_AUTH;

    private void releaseSession() {
        //之后需要释放session
        Map<String, Object> info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("qq", Long.parseLong(qq));
        String release = HttpKit.post(baseURL + "/release", JsonUtils.getJsonString(info));
        logger.info("释放：" + release);
    }

    private void initSession() {
        Map<String, Object> info = new HashMap<>();
        info.put("authKey", qqAuth);
        String result = HttpKit.post(baseURL + "/auth", JsonUtils.getJsonString(info));
        Constant.session = JsonUtils.getJsonObjectByJsonString(result).getString("session");
        info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("qq", Long.parseLong(qq));
        HttpKit.post(baseURL + "/verify", JsonUtils.getJsonString(info));
    }

    private MiraiUtil() {
        logger.info("初始化ing...");
    }

    public static ThreadLocal<MiraiUtil> instance = ThreadLocal.withInitial(MiraiUtil::new);

    public static MiraiUtil getInstance() {
        MiraiUtil context = instance.get();
        if (null == context) {
            logger.info("实例被删了...准备重新初始化...");
            context = init();
        }
        return context;
    }

    private static MiraiUtil init() {
        MiraiUtil context = new MiraiUtil();
        instance.set(context);
        return context;
    }

    // 后面写API接口，参考文档：https://gitee.com/dullwolf/mirai-api-http/blob/master/docs/API.md

    /**
     * 获取群列表
     */
    public List<JSONObject> getGroupList() {
        initSession();
        String URL = baseURL + "/groupList?sessionKey=" + Constant.session;
        String result = HttpKit.get(URL);
        releaseSession();
        return JsonUtils.getJsonArrayByJsonString(result);
    }

    /**
     * 获取好友列表
     */
    public List<JSONObject> getFriendList() {
        initSession();
        String URL = baseURL + "/friendList?sessionKey=" + Constant.session;
        String result = HttpKit.get(URL);
        releaseSession();
        return JsonUtils.getJsonArrayByJsonString(result);
    }

}

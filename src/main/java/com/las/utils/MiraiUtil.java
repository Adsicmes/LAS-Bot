package com.las.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.dto.CqResponse;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dullwolf
 */
public class MiraiUtil {

    private static Logger logger = Logger.getLogger(MiraiUtil.class);

    private static String baseURL = AppConfigs.MIRAT_API_URL;
    private static String qq = AppConfigs.BOT_QQ;
    private static String qqAuth = AppConfigs.KEY_AUTH;

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

    public CqResponse sendImgMsg(Long id, Long gId, ArrayList<String> urls, String type) {
        initSession();
        String URL;
        Map<String, Object> info = new HashMap<>();
        switch (type) {
            case "group":
                URL = baseURL + "/sendImageMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("group", gId);
                info.put("target", gId);
                info.put("urls", urls);

                break;
            case "discuss":
                URL = baseURL + "/sendImageMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("group", gId);
                info.put("urls", urls);
                break;
            case "private":
                URL = baseURL + "/sendImageMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("urls", urls);
                break;
            default:
                return null;
        }
        String result = HttpKit.post(URL, JsonUtils.getJsonString(info));
        logger.info("CQ结果：" + result);
        //可能发群消息有风控！如果拿不到消息ID，开启临时会发，私聊发给用户
        releaseSession();
        return null;
    }


    public CqResponse sendMsg(Long id, Long gId, ArrayList<JSONObject> msgList, String type) {
        initSession();
        String URL;
        Map<String, Object> info = new HashMap<>();
        switch (type) {
            case "group":
                URL = baseURL + "/sendGroupMessage";
                info.put("sessionKey", Constant.session);
                info.put("target", gId);
                info.put("messageChain", msgList);

                break;
            case "discuss":
                URL = baseURL + "/sendTempMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("group", gId);
                info.put("messageChain", msgList);
                break;
            case "private":
                URL = baseURL + "/sendFriendMessage";
                info.put("sessionKey", Constant.session);
                info.put("target", id);
                info.put("messageChain", msgList);
                break;
            default:
                return null;
        }
        String result = HttpKit.post(URL, JsonUtils.getJsonString(info));
        logger.info("CQ结果：" + result);
        //可能发群消息有风控！如果拿不到消息ID，开启临时会发，私聊发给用户
        releaseSession();
        return JsonUtils.getObjectByJson(result, CqResponse.class);
    }



    public void agreeFriend(JSONObject obj) {
        initSession();
        Map<String, Object> info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("operate", 0);
        info.put("eventId", obj.getLongValue("eventId"));
        info.put("fromId", obj.getLongValue("fromId"));
        info.put("groupId", obj.getLongValue("groupId"));
        info.put("message", "Hello");
        String result = HttpKit.post(baseURL + "/resp/newFriendRequestEvent", JsonUtils.getJsonString(info));
        logger.info("同意好友：" + result);
        releaseSession();
    }



    public void agreeGroup(JSONObject obj) {
        initSession();
        Map<String, Object> info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("eventId", obj.getLongValue("eventId"));
        info.put("fromId", obj.getLongValue("fromId"));
        info.put("groupId", obj.getLongValue("groupId"));
        info.put("operate", 0);
        info.put("message", "Hi");
        HttpKit.post(baseURL + "/resp/botInvitedJoinGroupRequestEvent", JsonUtils.getJsonString(info));
        releaseSession();
    }

}

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
public class MiRaiUtil {

    private static Logger logger = Logger.getLogger(MiRaiUtil.class);

    private static String baseURL = AppConfigs.miRaiApiUrl;
    private static String qq = AppConfigs.botQQ;
    private static String qqAuth = AppConfigs.keyAuth;

    /**
     * 锁
     */
    private final Object obj = new Object();


    public void initSession() {
        if (null == Constant.session) {
            synchronized (obj) {
                if (null == Constant.session) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("authKey", qqAuth);
                    String result = HttpKit.post(baseURL + "/auth", JsonUtils.getJsonString(info));
                    Constant.session = JsonUtils.getJsonObjectByJsonString(result).getString("session");
                    info = new HashMap<>();
                    info.put("sessionKey", Constant.session);
                    info.put("qq", Long.parseLong(qq));
                    HttpKit.post(baseURL + "/verify", JsonUtils.getJsonString(info));
                }
            }
        }
    }

    public void releaseSession() {
        if (null != Constant.session) {
            synchronized (obj) {
                if (null != Constant.session) {
                    Constant.oldSession = Constant.session;
                    Constant.session = null;
                }
            }
        }
    }

    private MiRaiUtil() {
        logger.info("初始化ing...");
    }

    private static ThreadLocal<MiRaiUtil> instance = ThreadLocal.withInitial(MiRaiUtil::new);

    public static MiRaiUtil getInstance() {
        MiRaiUtil context = instance.get();
        if (null == context) {
            logger.info("实例被删了...准备重新初始化...");
            context = init();
        }
        return context;
    }

    private static MiRaiUtil init() {
        MiRaiUtil context = new MiRaiUtil();
        instance.set(context);
        return context;
    }

    // 后面写API接口，参考文档：https://gitee.com/dullwolf/mirai-api-http/blob/master/docs/API.md

    /**
     * 获取群列表
     */
    public List<JSONObject> getGroupList() {
        String result = HttpKit.get(baseURL + "/groupList?sessionKey=" + Constant.session);
        return JsonUtils.getJsonArrayByJsonString(result);
    }

    /**
     * 获取好友列表
     */
    public List<JSONObject> getFriendList() {
        String result = HttpKit.get(baseURL + "/friendList?sessionKey=" + Constant.session);
        return JsonUtils.getJsonArrayByJsonString(result);
    }

    public CqResponse sendImgMsg(Long id, Long gId, ArrayList<String> urls, String type) {
        String apiUrl;
        Map<String, Object> info = new HashMap<>();
        switch (type) {
            case "group":
                apiUrl = baseURL + "/sendImageMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("group", gId);
                info.put("target", gId);
                info.put("urls", urls);

                break;
            case "discuss":
                apiUrl = baseURL + "/sendImageMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("group", gId);
                info.put("urls", urls);
                break;
            case "private":
                apiUrl = baseURL + "/sendImageMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("urls", urls);
                break;
            default:
                return null;
        }
        String result = HttpKit.post(apiUrl, JsonUtils.getJsonString(info));
        logger.info("CQ结果：" + result);
        //可能发群消息有风控！如果拿不到消息ID，开启临时会发，私聊发给用户
        return null;
    }


    public CqResponse sendMsg(Long id, Long gId, ArrayList<JSONObject> msgList, String type) {
        String apiUrl;
        Map<String, Object> info = new HashMap<>();
        switch (type) {
            case "group":
                apiUrl = baseURL + "/sendGroupMessage";
                info.put("sessionKey", Constant.session);
                info.put("target", gId);
                info.put("messageChain", msgList);

                break;
            case "discuss":
                apiUrl = baseURL + "/sendTempMessage";
                info.put("sessionKey", Constant.session);
                info.put("qq", id);
                info.put("group", gId);
                info.put("messageChain", msgList);
                break;
            case "private":
                apiUrl = baseURL + "/sendFriendMessage";
                info.put("sessionKey", Constant.session);
                info.put("target", id);
                info.put("messageChain", msgList);
                break;
            default:
                return null;
        }
        String result = HttpKit.post(apiUrl, JsonUtils.getJsonString(info));
        logger.info("CQ结果：" + result);
        //可能发群消息有风控！如果拿不到消息ID，开启临时会发，私聊发给用户
        return JsonUtils.getObjectByJson(result, CqResponse.class);
    }


    /**
     * 好友申请同意
     *
     * @param obj
     */
    public void agreeFriend(JSONObject obj) {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("operate", 0);
        info.put("eventId", obj.getLongValue("eventId"));
        info.put("fromId", obj.getLongValue("fromId"));
        info.put("groupId", obj.getLongValue("groupId"));
        info.put("message", "Hello");
        String result = HttpKit.post(baseURL + "/resp/newFriendRequestEvent", JsonUtils.getJsonString(info));
        logger.info("同意好友：" + result);
    }


    /**
     * 邀请进群同意
     *
     * @param obj
     */
    public void agreeGroup(JSONObject obj) {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("eventId", obj.getLongValue("eventId"));
        info.put("fromId", obj.getLongValue("fromId"));
        info.put("groupId", obj.getLongValue("groupId"));
        info.put("operate", 0);
        info.put("message", "Hi");
        HttpKit.post(baseURL + "/resp/botInvitedJoinGroupRequestEvent", JsonUtils.getJsonString(info));
    }

    /**
     * 获取群好友列表
     */
    public List<JSONObject> getGroupUsers(Long gId) {
        String result = HttpKit.get(baseURL + "/memberList?sessionKey=" + Constant.session + "&target=" + gId);
        logger.info("获取群[" + gId + "]好友列表信息：" + result);
        return JsonUtils.getJsonArrayByJsonString(result);
    }

    /**
     * 撤回消息
     */
    public void reCall(Long id) {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionKey", Constant.session);
        info.put("target", id);
        String result = HttpKit.post(baseURL + "/recall", JsonUtils.getJsonString(info));
        logger.info("撤回消息响应结果：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("code") == 5 && null != Constant.oldSession) {
            // 有可能消息ID在原先的会话，再尝试
            info = new HashMap<>();
            info.put("sessionKey", Constant.oldSession);
            info.put("target", id);
            String result2 = HttpKit.post(baseURL + "/recall", JsonUtils.getJsonString(info));
            logger.info("撤回消息响应结果2：" + result2);
        }
    }

}

package com.las.utils.mirai;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.dto.CqResponse;
import com.las.utils.JsonUtils;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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
    private static ReentrantLock lock = new ReentrantLock();


    public void initSession() {
        if (null == Constant.tempSession) {
            lock.lock();
            try {
                if (null == Constant.tempSession) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("authKey", qqAuth);
                    String result = HttpKit.post(baseURL + "/auth", JsonUtils.getJsonString(info));
                    Constant.session = JsonUtils.getJsonObjectByJsonString(result).getString("session");
                    Constant.tempSession = Constant.session;
                    info = new HashMap<>();
                    info.put("sessionKey", Constant.session);
                    info.put("qq", Long.parseLong(qq));
                    HttpKit.post(baseURL + "/verify", JsonUtils.getJsonString(info));
                }
            } catch (Exception e) {
                logger.error("初始化mirai会话报错：" + e.getMessage());
            } finally {
                lock.unlock();
            }
        }
    }

    public void releaseSession() {
        if (null != Constant.tempSession) {
            lock.lock();
            try {
                if (null != Constant.tempSession) {
                    Constant.oldSession = Constant.session;
                    Constant.tempSession = null;
                }
            } catch (Exception ignored) {
            } finally {
                lock.unlock();
            }
        }
    }

    private MiRaiUtil() {
        logger.debug("初始化mirai实例ing...");
    }

    private static ThreadLocal<MiRaiUtil> instance = ThreadLocal.withInitial(MiRaiUtil::new);

    public static MiRaiUtil getInstance() {
        MiRaiUtil context = instance.get();
        if (null == context) {
            logger.warn("mirai实例被删了...准备重新初始化...");
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

    public CqResponse sendImgMsg(Long id, Long gId, List<String> urls, String type) {
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
        HttpKit.post(apiUrl, JsonUtils.getJsonString(info));
        //可能发群消息有风控！如果拿不到消息ID，开启临时会发，私聊发给用户
        return null;
    }


    public CqResponse sendMsg(Long id, Long gId, List<JSONObject> msgList, String type) {
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
        //可能发群消息有风控！如果拿不到消息ID，开启临时会发，私聊发给用户
        return JsonUtils.getObjectByJson(result, CqResponse.class);
    }

    public CqResponse sendVoiceorImgMsg(Long id, Long gId, String url, String type, int tag) {
        //音频的发送，私聊发似乎不行了，查API 腾讯那边也没有文档
        File file = null;
        try {
            String URL = baseURL + "/uploadImage";
            if (tag == 0) {
                URL = baseURL + "/uploadImage";
            } else if (tag == 1) {
                URL = baseURL + "/uploadVoice";
            }
            Map<String, String> info = new HashMap<>();
            info.put("sessionKey", Constant.session);
            String fileType = "group";
            switch (type) {
                case "group":
                    break;
                case "discuss":
                    fileType = "temp";
                    break;
                case "private":
                    fileType = "friend";
                    break;
                default:
                    return null;
            }
            if (tag == 0) {
                info.put("type", fileType);
            } else if (tag == 1) {
                //语音上传只支持group
                info.put("type", "group");
            }


            String fileName = UUID.randomUUID().toString();
            if (tag == 0) {
                fileName = fileName + ".jpg";
            } else if (tag == 1) {
                fileName = fileName + ".amr";
            }

            BufferedInputStream bis = new BufferedInputStream(Objects.requireNonNull(HttpUtil.getFileInputStream(url)));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
            int b;
            while (-1 != (b = bis.read())) {
                bos.write(b);
            }
            bos.close();
            bis.close();

            ArrayList<JSONObject> msgList = new ArrayList<>();
            file = new File(fileName);

            Map<String, File> info2 = new HashMap<>();
            if (tag == 0) {
                info2.put("img", file);
            } else if (tag == 1) {
                info2.put("voice", file);
            }
            StringBuilder sb = new StringBuilder();
            BufferedInputStream bis2 = new BufferedInputStream(HttpUtil.postFile(URL, info, info2));
            int len;
            byte[] bytes = new byte[1024];
            while (-1 != (len = bis2.read(bytes))) {
                String str = new String(bytes, 0, len);
                sb.append(str);
            }
            bis2.close();
            if (tag == 0) {
                JSONObject data = JsonUtils.getJsonObjectByJsonString(sb.toString());
                JSONObject object = new JSONObject();
                object.put("type", "Image");
                object.put("imageId", data.getString("imageId"));
                msgList.add(object);
            } else if (tag == 1) {

                JSONObject data = JsonUtils.getJsonObjectByJsonString(sb.toString());
                JSONObject object = new JSONObject();
                object.put("type", "Voice");
                object.put("voiceId", data.getString("voiceId"));
                msgList.add(object);
            }


            Map<String, Object> info3 = new HashMap<>();
            switch (type) {
                case "group":
                    URL = baseURL + "/sendGroupMessage";
                    info3.put("sessionKey", Constant.session);
                    info3.put("target", gId);
                    info3.put("messageChain", msgList);

                    break;
                case "discuss":
                    URL = baseURL + "/sendTempMessage";
                    info3.put("sessionKey", Constant.session);
                    info3.put("qq", id);
                    info3.put("group", gId);
                    info3.put("messageChain", msgList);
                    break;
                case "private":
                    URL = baseURL + "/sendFriendMessage";
                    info3.put("sessionKey", Constant.session);
                    info3.put("target", id);
                    info3.put("messageChain", msgList);
                    break;
                default:
                    return null;
            }

            HttpKit.post(URL, JsonUtils.getJsonString(info3));

        } catch (Exception ignored) {
        } finally {
            //最后删除文件
            if (null != file) {
                file.delete();
            }
        }

        return null;

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
        logger.debug("同意好友：" + result);
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
        logger.debug("获取群[" + gId + "]好友列表信息：" + result);
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
        logger.debug("撤回消息响应结果：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("code") == 5 && null != Constant.oldSession) {
            // 有可能消息ID在原先的会话，再尝试
            info = new HashMap<>();
            info.put("sessionKey", Constant.oldSession);
            info.put("target", id);
            String result2 = HttpKit.post(baseURL + "/recall", JsonUtils.getJsonString(info));
            logger.debug("撤回消息响应结果2：" + result2);
        }
    }

}

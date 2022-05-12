package com.las.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.common.Constant;
import com.las.dto.CqResponse;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdUtil {

    private static Logger logger = Logger.getLogger(CmdUtil.class);

    /**
     * CQ发送消息
     *
     * @param msg  消息内容
     * @param id   ID
     * @param type 消息类型
     */
    public static CqResponse sendMessage(String msg, Long userId, Long id, int type) {
        CqResponse response = null;
        //和Cq发消息内容不一样，Mirai采取用消息链，后续封装其他方法，例如发送语音消息、图片消息
        ArrayList<JSONObject> msgList = new ArrayList<>();
        JSONObject object = new JSONObject();
        object.put("type", "Plain");
        object.put("text", msg);
        msgList.add(object);
        response = getCqResponse(userId, id, type, msgList);
        return response;
    }

    public static CqResponse send163MusicMessage(JSONObject obj, Long userId, Long id, int type) {
        CqResponse response = null;
        JSONObject info = new JSONObject();
        JSONArray authors = obj.getJSONArray("ar");
        if (authors.size() > 0) {
            //默认取第一个作者
            JSONObject author = authors.getJSONObject(0);
            String name = author.getString("name");
            info.put("summary", name);
        }
        String songName = obj.getString("name");
        info.put("brief", "[分享]" + songName);
        info.put("kind", "NeteaseCloudMusic");
        info.put("musicUrl", "http://music.163.com/song/media/outer/url?id=" + obj.getString("id") + "&userid=558417883&sc=wm");//用户是蠢狼网易云APP的账号，我分享形成的小卡片
        String picUrl = obj.getJSONObject("al").getString("picUrl");
        info.put("pictureUrl", picUrl);
        info.put("type", "MusicShare");
        info.put("title", songName);
        info.put("jumpUrl", "https://y.music.163.com/m/song?id=" + obj.getString("id"));
        logger.info("搜索到歌曲内容是：" + info.toJSONString());

        ArrayList<JSONObject> msgList = new ArrayList<>();
        msgList.add(info);

        response = getCqResponse(userId, id, type, msgList);
        return response;

    }


    public static CqResponse sendAtMessage(String msg, Long atId, Long userId, Long id, int type) {
        CqResponse response = null;
        ArrayList<JSONObject> msgList = new ArrayList<>();
        JSONObject object2 = new JSONObject();
        object2.put("type", "At");
        object2.put("target", atId);
        msgList.add(object2);
        JSONObject object = new JSONObject();
        object.put("type", "Plain");
        object.put("text", " " + msg.trim());//因为前面有AT，加下空格
        msgList.add(object);
        response = getCqResponse(userId, id, type, msgList);
        return response;
    }

    private static CqResponse getCqResponse(Long userId, Long id, int type, ArrayList<JSONObject> msgList) {
        CqResponse response = null;
        switch (type) {
            case Constant.MESSAGE_TYPE_PRIVATE:
                response = MiraiUtil.getInstance().sendMsg(userId, id, msgList, "private");
                break;
            case Constant.MESSAGE_TYPE_GROUP:
                response = MiraiUtil.getInstance().sendMsg(userId, id, msgList, "group");
                break;
            case Constant.MESSAGE_TYPE_DISCUSS:
                response = MiraiUtil.getInstance().sendMsg(userId, id, msgList, "discuss");
                break;
            default:
                break;
        }
        return response;
    }

    public static CqResponse sendImgMessage(ArrayList<String> urls, Long userId, Long id, int type) {
        CqResponse response = null;

        switch (type) {
            case Constant.MESSAGE_TYPE_PRIVATE:
                response = MiraiUtil.getInstance().sendImgMsg(userId, id, urls, "private");
                break;
            case Constant.MESSAGE_TYPE_GROUP:
                response = MiraiUtil.getInstance().sendImgMsg(userId, id, urls, "group");
                break;
            case Constant.MESSAGE_TYPE_DISCUSS:
                response = MiraiUtil.getInstance().sendImgMsg(userId, id, urls, "discuss");
                break;
            default:
                break;
        }
        return response;
    }


}

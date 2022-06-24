package com.las.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.las.common.Constant;
import com.las.dto.CqResponse;
import com.las.dto.music.Music163DTO;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dullwolf
 */
public class CmdUtil {

    private static Logger logger = Logger.getLogger(CmdUtil.class);

    private static CqResponse getCqResponse(Long userId, Long id, int type, ArrayList<JSONObject> msgList) {
        CqResponse response = null;
        switch (type) {
            case Constant.MESSAGE_TYPE_PRIVATE:
                response = MiRaiUtil.getInstance().sendMsg(userId, id, msgList, "private");
                break;
            case Constant.MESSAGE_TYPE_GROUP:
                response = MiRaiUtil.getInstance().sendMsg(userId, id, msgList, "group");
                break;
            case Constant.MESSAGE_TYPE_DISCUSS:
                response = MiRaiUtil.getInstance().sendMsg(userId, id, msgList, "discuss");
                break;
            default:
                break;
        }
        return response;
    }

    /**
     * CQ发送消息
     *
     * @param msg     消息内容
     * @param userId  用户ID
     * @param id      组ID
     * @param type    消息类型
     */
    public static CqResponse sendMessage(String msg, Long userId, Long id, int type) {
        CqResponse response;
        ArrayList<JSONObject> msgList = new ArrayList<>();
        JSONObject object = new JSONObject();
        object.put("type", "Plain");
        object.put("text", msg);
        msgList.add(object);
        response = getCqResponse(userId, id, type, msgList);
        return response;
    }

    /**
     * CQ发送网易云消息
     *
     * @param songObj 封装的网易163DTO
     * @param userId  用户ID
     * @param id      组ID
     * @param type    消息类型
     */
    public static CqResponse send163MusicMessage(Music163DTO songObj, Long userId, Long id, int type) {
        CqResponse response;
        JSONObject info = new JSONObject();
        JSONArray authors = songObj.getAr();
        if (authors.size() > 0) {
            //默认取第一个作者
            JSONObject author = authors.getJSONObject(0);
            String name = author.getString("name");
            info.put("summary", name);
        }
        info.put("brief", "[分享]" + songObj.getName());
        info.put("kind", "NeteaseCloudMusic");
        info.put("musicUrl", "http://music.163.com/song/media/outer/url?id=" + songObj.getId() + "&userid=558417883&sc=wm");
        String picUrl = songObj.getAl().getPicUrl();
        info.put("pictureUrl", picUrl);
        info.put("type", "MusicShare");
        info.put("title", songObj.getName());
        info.put("jumpUrl", "https://y.music.163.com/m/song?id=" + songObj.getId());
        logger.info("搜索到歌曲内容是：" + info.toJSONString());
        ArrayList<JSONObject> msgList = new ArrayList<>();
        msgList.add(info);
        response = getCqResponse(userId, id, type, msgList);
        return response;

    }

    /**
     * CQ发送AT消息
     *
     * @param msg     消息内容
     * @param atId    @用户ID
     * @param userId  用户ID
     * @param id      组ID
     * @param type    消息类型
     */
    public static CqResponse sendAtMessage(String msg, Long atId, Long userId, Long id, int type) {
        CqResponse response;
        ArrayList<JSONObject> msgList = new ArrayList<>();
        JSONObject object2 = new JSONObject();
        object2.put("type", "At");
        object2.put("target", atId);
        msgList.add(object2);
        JSONObject object = new JSONObject();
        object.put("type", "Plain");
        //因为前面有AT，加下空格
        object.put("text", " " + msg.trim());
        msgList.add(object);
        response = getCqResponse(userId, id, type, msgList);
        return response;
    }

    /**
     * CQ发送图片消息
     *
     * @param urls    网络图URL
     * @param userId  用户ID
     * @param id      组ID
     * @param type    消息类型
     */
    public static CqResponse sendImgMessage(ArrayList<String> urls, Long userId, Long id, int type) {
        CqResponse response = null;

        switch (type) {
            case Constant.MESSAGE_TYPE_PRIVATE:
                response = MiRaiUtil.getInstance().sendImgMsg(userId, id, urls, "private");
                break;
            case Constant.MESSAGE_TYPE_GROUP:
                response = MiRaiUtil.getInstance().sendImgMsg(userId, id, urls, "group");
                break;
            case Constant.MESSAGE_TYPE_DISCUSS:
                response = MiRaiUtil.getInstance().sendImgMsg(userId, id, urls, "discuss");
                break;
            default:
                break;
        }
        return response;
    }


}

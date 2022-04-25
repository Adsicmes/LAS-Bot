package com.las.cmd;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.utils.CmdUtil;
import com.las.utils.JsonUtils;
import com.las.utils.netease.NeteaseMusicAPI;
import org.apache.log4j.Logger;

import java.util.ArrayList;

@BotCmd
public class SongCmd extends Command {

    private static Logger logger = Logger.getLogger(SongCmd.class);


    public SongCmd() {
        super("点歌");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) {
        try {
            //开始实现点歌功能
            String songName = null;
            if (args.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : args) {
                    sb.append(s).append(" ");
                }
                songName = sb.toString();
            }
            if (StrKit.notBlank(songName)) {
                //<>《》！:#! 用户习惯带着些符号，需要屏蔽为空格
                songName = songName.replaceAll("<", " ");
                songName = songName.replaceAll(">", " ");
                songName = songName.replaceAll("《", " ");
                songName = songName.replaceAll("》", " ");
                songName = songName.replaceAll("！", " ");
                songName = songName.replaceAll(":", " ");
                songName = songName.replaceAll("：", " ");
                songName = songName.replaceAll("#", " ");
                songName = songName.replaceAll("!", " ");
                String search = NeteaseMusicAPI.search(songName);
                JSONObject object = JsonUtils.getJsonObjectByJsonString(search);
                assert object != null;
                if (null != object.get("result")) {
                    JSONObject obj = JsonUtils.getJsonObject(object.get("result"));
                    if (null != obj.get("songs")) {
                        JSONArray array = obj.getJSONArray("songs");
                        if (!array.isEmpty()) {
                            JSONObject song = JsonUtils.getJsonObject(array.get(0));//默认就取第一首歌
                            CmdUtil.send163MusicMessage(song, userId, id, type);
                        }
                    } else {
                        String msg = songName + " 找不到该歌曲>.<";
                        CmdUtil.sendMessage(msg, userId, id, type);
                    }
                } else {
                    String msg = songName + " 找不到该歌曲>.<";
                    CmdUtil.sendMessage(msg, userId, id, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(super.toString() + "执行时报错，命令内容:" + command);
        }
    }

}
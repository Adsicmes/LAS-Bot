package com.las.cmd.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.cmd.BaseCommand;
import com.las.common.Constant;
import com.las.dto.music.Music163AlDTO;
import com.las.dto.music.Music163DTO;
import com.las.utils.CmdUtil;
import com.las.utils.JsonUtils;
import com.las.utils.NetEaseMusicUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * @author dullwolf
 */
//@BotCmd(funName = "点歌功能")
public class SongCmd extends BaseCommand {

    private static Logger logger = Logger.getLogger(SongCmd.class);


    public SongCmd() {
        super(Constant.MIN_PRIORITY,"点歌");
    }

    @Override
    public void execute(Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
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
            String search = NetEaseMusicUtil.search(songName);
            JSONObject object = JsonUtils.getJsonObjectByJsonString(search);
            assert object != null;
            if (null != object.get("result")) {
                JSONObject obj = JsonUtils.getJsonObject(object.get("result"));
                if (null != obj.get("songs")) {
                    JSONArray array = obj.getJSONArray("songs");
                    if (!array.isEmpty()) {
                        //默认就取第一首歌
                        JSONObject song = JsonUtils.getJsonObject(array.get(0));
                        Music163DTO dto = new Music163DTO();
                        dto.setId(song.getString("id"));
                        dto.setAr(song.getJSONArray("ar"));
                        dto.setName(song.getString("name"));
                        Music163AlDTO alDTO = new Music163AlDTO();
                        alDTO.setPicUrl(song.getJSONObject("al").getString("picUrl"));
                        dto.setAl(alDTO);
                        CmdUtil.send163MusicMessage(dto, userId, id, type);
                    }
                } else {
                    String msg = songName + " 找不到该歌曲>.<";
                    CmdUtil.sendMessage(msg, userId, id, type);
                }
            } else {
                String msg = songName + " 找不到该歌曲>.<";
                CmdUtil.sendMessage(msg, userId, id, type);
            }
        } else {
            String msg = "请在指令后面带歌曲名>.<";
            CmdUtil.sendMessage(msg, userId, id, type);
        }
    }

}
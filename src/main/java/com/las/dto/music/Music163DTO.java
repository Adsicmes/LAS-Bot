package com.las.dto.music;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 使用爬虫工具爬163网易音乐返回的DTO
 * @author dullwolf
 */
public class Music163DTO {

    /**
     * 作曲者集合
     */
    private List<JSONObject> ar;

    /**
     * 歌曲名
     */
    private String name;

    /**
     * 歌曲ID
     */
    private String id;

    /**
     * 歌曲详情
     */
    private Music163AlDTO al;


    public List<JSONObject> getAr() {
        return ar;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Music163AlDTO getAl() {
        return al;
    }
}

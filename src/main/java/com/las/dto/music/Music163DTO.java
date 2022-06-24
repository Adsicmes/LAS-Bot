package com.las.dto.music;

import com.alibaba.fastjson.JSONArray;

/**
 * 使用爬虫工具爬163网易音乐返回的DTO
 * @author dullwolf
 */
public class Music163DTO {

    /**
     * 作曲者集合
     */
    private JSONArray ar;

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


    public JSONArray getAr() {
        return ar;
    }

    public void setAr(JSONArray ar) {
        this.ar = ar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Music163AlDTO getAl() {
        return al;
    }

    public void setAl(Music163AlDTO al) {
        this.al = al;
    }
}

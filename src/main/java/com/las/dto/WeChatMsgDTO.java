package com.las.dto;

/**
 * @author dullwolf
 */
public class WeChatMsgDTO {

    private String id;
    private String wxid;
    private String content;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "WeChatMsgDTO{" +
                "id='" + id + '\'' +
                ", wxid='" + wxid + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}

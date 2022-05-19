package com.las.model;

import com.las.annotation.Column;
import com.las.annotation.Table;

@Table("user")
public class User {

    private Long id;

    @Column("user_id")
    private Long userId;

    private String nickname;

    private String remark;

    @Column("fun_permission")
    private Integer funPermission;

    @Column("bot_qq")
    private Long botQQ;

    @Column("used_count")
    private Integer usedCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getFunPermission() {
        return funPermission;
    }

    public void setFunPermission(Integer funPermission) {
        this.funPermission = funPermission;
    }

    public Long getBotQQ() {
        return botQQ;
    }

    public void setBotQQ(Long botQQ) {
        this.botQQ = botQQ;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
}

package com.las.model;

import com.las.annotation.Column;
import com.las.annotation.Table;

/**
 * @author dullwolf
 */
@Table("group_fun")
public class GroupFun {

    private Long id;

    @Column("group_id")
    private Long groupId;

    @Column("group_fun")
    private String groupFun;

    @Column("is_enable")
    private Integer isEnable;

    @Column("bot_qq")
    private Long botQQ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupFun() {
        return groupFun;
    }

    public void setGroupFun(String groupFun) {
        this.groupFun = groupFun;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public Long getBotQQ() {
        return botQQ;
    }

    public void setBotQQ(Long botQQ) {
        this.botQQ = botQQ;
    }
}

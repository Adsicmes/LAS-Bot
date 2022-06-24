package com.las.model;

import com.las.annotation.Column;
import com.las.annotation.Table;

/**
 * @author dullwolf
 */
@Table("group")
public class Group {

    private Long id;

    private String name;

    @Column("group_id")
    private Long groupId;

    @Column("group_role")
    private String groupRole;

    @Column("bot_qq")
    private Long botQQ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(String groupRole) {
        this.groupRole = groupRole;
    }

    public Long getBotQQ() {
        return botQQ;
    }

    public void setBotQQ(Long botQQ) {
        this.botQQ = botQQ;
    }
}

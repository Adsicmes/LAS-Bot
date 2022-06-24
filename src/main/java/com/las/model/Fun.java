package com.las.model;

import com.las.annotation.Column;
import com.las.annotation.Table;

/**
 * @author dullwolf
 */
@Table("fun")
public class Fun {

    private Long id;

    @Column("fun_name")
    private String funName;

    @Column("fun_weight")
    private Integer funweight;

    @Column("bot_qq")
    private Long botQQ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public Integer getFunweight() {
        return funweight;
    }

    public void setFunweight(Integer funweight) {
        this.funweight = funweight;
    }

    public Long getBotQQ() {
        return botQQ;
    }

    public void setBotQQ(Long botQQ) {
        this.botQQ = botQQ;
    }
}

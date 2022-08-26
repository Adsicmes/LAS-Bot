package com.las.cq.retdata;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class CookiesData {
    @JSONField(name = "cookies")
    private String cookies;
}

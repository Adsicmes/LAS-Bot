package com.las.cq.retdata;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class MessageData {
    @JSONField(name = "message_id")
    private int messageId;
}

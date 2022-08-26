package com.las.cq.event.meta;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.las.cq.event.CQEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class CQMetaEvent extends CQEvent {
    /**
     * heartbeat	元事件类型
     */
    @JSONField(name = "meta_event_type")
    private String metaEventType;
}

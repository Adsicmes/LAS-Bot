package com.las.utils;


import com.alibaba.fastjson.JSONObject;
import com.las.config.AppConfigs;
import com.las.dto.WeChatMsgDTO;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author dullwolf
 */
public class WxCmdUtil {

    private static Logger logger = Logger.getLogger(WxCmdUtil.class);

    private static void sendMsg(String json) {
        try {
            AppConfigs.WX_PUSH_SERVER.send(json);
        } catch (Exception e) {
            logger.error("发送WX信息失败，原因：" + e.getMessage());
        }
    }

    public static void getContact() {
        String id = String.valueOf(System.currentTimeMillis());
        WeChatMsgDTO msgDTO = new WeChatMsgDTO();
        msgDTO.setContent("op:list member");
        msgDTO.setWxid("null");
        msgDTO.setType(5010);
        msgDTO.setId(id);
        String json = JSONObject.toJSONString(msgDTO);
        logger.info("微信getContact:" + json);
        sendMsg(json);
    }

    public static void sendMsg(String wxId, String text) {
        String id = String.valueOf(System.currentTimeMillis());
        WeChatMsgDTO msgDTO = new WeChatMsgDTO();
        msgDTO.setContent(text);
        msgDTO.setWxid(wxId);
        msgDTO.setType(555);
        msgDTO.setId(id);
        String json = JSONObject.toJSONString(msgDTO);
        logger.info("微信sendMsg:" + json);
        sendMsg(json);
    }

}

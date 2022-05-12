package com.las.service.wx;

import com.alibaba.fastjson.JSONObject;
import com.las.dto.WeChatMsgDTO;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class WeChatPushService extends WebSocketClient {

    private static Logger logger = Logger.getLogger(WeChatPushService.class);

    public WeChatPushService(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("正在打开WX....");
    }

    @Override
    public void onMessage(String s) {
        logger.info("收到WX消息：" + s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.warn("微信服务已关闭");
    }

    @Override
    public void onError(Exception e) {
        logger.error("WX服务异常");
    }

    public void sendMsg(String wxid, String text) {
        String id = String.valueOf(new Date().getTime());
        WeChatMsgDTO msgDTO = new WeChatMsgDTO();
        msgDTO.setContent(text);
        msgDTO.setWxid(wxid);
        msgDTO.setType(555);
        msgDTO.setId(id);
        String json = JSONObject.toJSONString(msgDTO);
        logger.info("微信sendMsg:" + json);
        sendMsg(json);
    }

    public void getContact() {
        String id = String.valueOf(new Date().getTime());
        WeChatMsgDTO msgDTO = new WeChatMsgDTO();
        msgDTO.setContent("op:list member");
        msgDTO.setWxid("null");
        msgDTO.setType(5010);
        msgDTO.setId(id);
        String json = JSONObject.toJSONString(msgDTO);
        logger.info("微信getContact:" + json);
        sendMsg(json);
    }

    private void sendMsg(String json) {
        try {
            send(json);
        } catch (Exception e) {
            logger.error("发送WX信息失败，原因：" + e.getMessage());
        }
    }
}

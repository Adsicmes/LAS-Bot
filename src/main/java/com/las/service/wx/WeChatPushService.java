package com.las.service.wx;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

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
        logger.info("微信服务已关闭");
    }

    @Override
    public void onError(Exception e) {
        logger.info("WX服务异常");
    }

}

package com.las.service.wx;

import com.alibaba.fastjson.JSONObject;
import com.las.enums.MsgCallBackEnum;
import com.las.enums.WxMsgCallBackEnum;
import com.las.utils.JsonUtils;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
        JSONObject content = JSONObject.parseObject(s);
        logger.info(content);
        assert content != null;
        int type = content.getInteger("type");
        logger.info("微信事件类型是：" + type);
        String className = WxMsgCallBackEnum.getClassNameByType(type);
        if(null != className){
            try {
                Map<String, Object> params = JsonUtils.getMapByObject(content);
                Class<?> aClass = Class.forName(className);
                Object obj = aClass.newInstance();
                // 用反射机制拿handleMsg方法
                Method handleMsg = aClass.getMethod("handleMsg", Map.class);
                // 代理执行 obj.handleMsg()
                handleMsg.invoke(obj, params);
                // 用反射机制拿exec方法
                Method exec = aClass.getMethod("exec");
                exec.invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("执行微信事件类型报错，原因：" + e.toString());
            }
        }
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

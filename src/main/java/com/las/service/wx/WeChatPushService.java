package com.las.service.wx;

import com.alibaba.fastjson.JSONObject;
import com.las.dto.WeChatMsgDTO;
import com.las.enums.MsgCallBackEnum;
import com.las.enums.WxMsgCallBackEnum;
import com.las.utils.JsonUtils;
import com.las.utils.SpringUtils;
import com.las.utils.StrUtils;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

/**
 * @author dullwolf
 */
public class WeChatPushService extends WebSocketClient {

    private static Logger logger = Logger.getLogger(WeChatPushService.class);

    private static final int HEART_BEAT = 5005;             //服务器返回心跳包
    private static final int RECV_TXT_MSG = 1;              //收到的消息为文字消息
    private static final int RECV_PIC_MSG = 3;              //收到的消息为图片消息
    private static final int USER_LIST = 5000;              //发送消息类型为获取用户列表
    private static final int GET_USER_LIST_SUCCSESS = 5001; //获取用户列表成功
    private static final int GET_USER_LIST_FAIL     = 5002; //获取用户列表失败
    private static final int TXT_MSG = 555;                 //发送消息类型为文本
    private static final int PIC_MSG = 500;                 //发送消息类型为图片
    private static final int AT_MSG = 550;                  //发送群中@用户的消息
    private static final int CHATROOM_MEMBER = 5010;        //获取群成员
    private static final int CHATROOM_MEMBER_NICK = 5020;
    private static final int PERSONAL_INFO = 6500;
    private static final int DEBUG_SWITCH = 6000;
    private static final int PERSONAL_DETAIL =6550;
    private static final int DESTROY_ALL = 9999;

    private static final String ROOM_MEMBER_LIST = "op:list member";
    private static final String CONTACT_LIST = "user list";
    private static final String NULL_MSG = "null";

    public WeChatPushService(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("正在打开WX....");
    }

    @Override
    public void onMessage(String s) {
        //logger.info("收到WX消息：" + s);
        if(StrUtils.isBlank(s)){
            return;
        }
        JSONObject content = JSONObject.parseObject(s);
        //logger.info(content);
        assert content != null;
        int type = content.getInteger("type");
        logger.info("微信事件类型是：" + type);
        String className = WxMsgCallBackEnum.getClassNameByType(type);
        if (null != className) {
            try {
                Map<String, Object> params = JsonUtils.getMapByObject(content);
                Class<?> aClass = Class.forName(className);
                String simpleName = aClass.getSimpleName();
                String beanName = simpleName.substring(0,1).toLowerCase() + simpleName.substring(1);
                Object obj = SpringUtils.getBean(beanName);
                //Object obj = aClass.newInstance();
                // 用反射机制拿handleMsg方法
                Method handleMsg = aClass.getMethod("handleMsg", Map.class);
                // 代理执行 obj.handleMsg()
                handleMsg.invoke(obj, params);
                // 用反射机制拿exec方法
                Method exec = aClass.getMethod("exec");
                if (exec != null) {
                    exec.invoke(obj);
                    System.gc();
                }
            } catch (Exception e) {
                logger.error("执行微信事件类型报错，原因：" + e.toString());
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.debug("微信服务已关闭");
    }

    @Override
    public void onError(Exception e) {
        logger.debug("WX服务异常");
    }

    /**
     * 发送信息
     * @param json 要发送信息的json字符串
     */
    private void sendMsg(String json) {
        try {
            send(json);
        } catch (Exception e) {
            //发送消息失败！
            logger.info("发送消息失败！");
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取会话ID
     * @return
     */
    private String getSessionId(){
        return String.valueOf(new Date().getTime());
    }

    /**
     * 发送文本消息
     * @param wxid 个人的wxid或者群id（xxx@chatroom）
     * @param text 要发送的消息内容
     */
    public void sendTextMsg(String wxid, String text){
        //创建发送消息JSON
        WeChatMsgDTO msgDTO = new WeChatMsgDTO();
        msgDTO.setContent(text);
        msgDTO.setWxid(wxid);
        msgDTO.setType(TXT_MSG);
        msgDTO.setId(getSessionId());
        String json = JSONObject.toJSONString(msgDTO);
        logger.info("发送文本消息 --> " + json);
        sendMsg(json);
    }

//    /**
//     * 发送图片消息
//     * @param wxid  个人的wxid或者群id（xxx@chatroom）
//     * @param imgUrlStr 发送图片的绝对路径
//     */
//    public void sendImgMsg(String wxid, String imgUrlStr) {
//        //创建发送消息JSON
//        String json = WXMsg.builder()
//                .content(imgUrlStr)
//                .wxid(wxid)
//                .type(PIC_MSG)
//                .id(getSessionId())
//                .build()
//                .toJson();
//        logger.info("发送图片消息 --> " + json);
//        sendMsg(json);
//    }

//    /**
//     * 发送AT类型消息 ---> 暂不可用
//     */
//    public void sendAtMsg(String wxid, String roomId, String text){
//        //创建发送消息JSON
//        String json = WXMsg.builder()
//                .content(text)
//                .wxid(wxid)
//                .roomId(roomId)
//                .type(AT_MSG)
//                .id(getSessionId())
//                .build()
//                .toJson();
//        logger.info("发送微信群AT成员消息 --> " + json);
//        sendMsg(json);
//    }
//
//    /**
//     * 获取联系人列表
//     */
//    public void getContactList() {
//        //创建发送消息JSON
//        String json = WXMsg.builder()
//                .content(CONTACT_LIST)
//                .wxid(NULL_MSG)
//                .type(USER_LIST)
//                .id(getSessionId())
//                .build()
//                .toJson();
//        logger.info("发送获取联系人列表请求 --> " + json);
//        sendMsg(json);
//    }
//
//    /**
//     * 获取所有群成员列表
//     */
//    public void getRoomMemberList() {
//        //创建发送消息JSON
//        String json = WXMsg.builder()
//                .content(ROOM_MEMBER_LIST)
//                .wxid(NULL_MSG)
//                .type(CHATROOM_MEMBER)
//                .id(getSessionId())
//                .build()
//                .toJson();
//        logger.info("发送获取所有群成员列表请求 --> " + json);
//        sendMsg(json);
//    }

}

package com.las.event;

import com.alibaba.fastjson.JSONObject;
import com.las.annotation.WxEvent;
import com.las.enums.WxMsgCallBackEnum;
import com.las.strategy.handle.AbstractBotMsgHandler;
import com.las.strategy.wxhandle.AbstractWxBotMsgHandler;
import com.las.utils.WxCmdUtil;
import org.apache.log4j.Logger;

/**
 * 自定义QQ私聊消息事件（此类作为参考）
 *
 * @author dullwolf
 */
@WxEvent(event = WxMsgCallBackEnum.WX_FRIEND_MSG)
public class MyWxEvent extends AbstractWxBotMsgHandler {

    private static Logger logger = Logger.getLogger(MyWxEvent.class);

    @Override
    public void exec() {
        JSONObject msgData = getMsgData();
        logger.info("处理一条微信好友消息..." + msgData.toJSONString());

        String content = msgData.getString("content");
        if (content.startsWith("123")) {
            String wxid = msgData.getString("wxid");
            logger.info("准备发送消息，wxID是？" + wxid);
            WxCmdUtil.sendMsg(wxid, "测试成功");
        }


    }

}

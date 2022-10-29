package com.las.strategy.wxhandle;

import com.alibaba.fastjson.JSONObject;
import com.las.utils.EmojiUtil;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author dullwolf
 */
@Component
@Scope("prototype")
public class WxFriendMsgHandler extends AbstractWxBotMsgHandler {

    private static Logger logger = Logger.getLogger(WxFriendMsgHandler.class);

    @Override
    public void exec() {
        JSONObject msgData = getMsgData();
        logger.info("收到微信消息：" + msgData.toJSONString());
        String receiver = msgData.getString("receiver");
        String sender = msgData.getString("sender");
        String wxId = null;
        if ("self".equals(receiver)) {
            //说明是私聊发送给我的
            wxId = sender;
        } else {
            //否则就是群聊，公众号等，群里的是以@chatroom结尾
            if (receiver.endsWith("@chatroom")) {
                wxId = receiver;
            }
        }
        if (null != wxId) {
            // 确保微信ID不为空，再去判断指令发送消息
            String content = msgData.getString("content");
            // 有表情需要处理
            String command = EmojiUtil.emojiChange(content);
            exeCommand(wxId, command);
        }
    }
}

package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.utils.mirai.MiRaiUtil;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author dullwolf
 */
@Component
@Scope("prototype")
public class NewFriendRequestsMsgHandler extends AbstractBotMsgHandler {

    private static Logger logger = Logger.getLogger(NewFriendRequestsMsgHandler.class);

    @Override
    public void exec() {
        JSONObject object = getCqObj();
        logger.info(object.toJSONString());
        //添加好友
        MiRaiUtil.getInstance().agreeFriend(object);
    }
}

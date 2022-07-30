package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.utils.mirai.MiRaiUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author dullwolf
 */
@Component
public class BotInvitedJoinGroupRequestsMsgHandler extends AbstractBotMsgHandler {

    private static Logger logger = Logger.getLogger(BotInvitedJoinGroupRequestsMsgHandler.class);

    @Override
    public void exec() {
        JSONObject object = getCqObj();
        logger.info(object.toJSONString());
        //邀请进群
        MiRaiUtil.getInstance().agreeGroup(object);
        //初始化群权限
        initBotFun();
    }
}

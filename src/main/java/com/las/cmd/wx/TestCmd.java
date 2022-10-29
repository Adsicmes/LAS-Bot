package com.las.cmd.wx;

import com.alibaba.fastjson.JSONObject;
import com.las.annotation.WxCmd;
import com.las.cmd.BaseWxCommand;
import com.las.dao.UserDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

//@WxCmd
public class TestCmd extends BaseWxCommand {

    private static Logger logger = Logger.getLogger(TestCmd.class);

    @Autowired
    private UserDao userDao;

    @Override
    public void execute(JSONObject msgObj, String wxId, String command) throws Exception {
        logger.info("处理微信消息：" + msgObj.toJSONString());


    }

}

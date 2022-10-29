package com.las.utils;


import com.las.config.AppConfigs;
import org.apache.log4j.Logger;

/**
 * @author dullwolf
 */
public class WxCmdUtil {

    private static Logger logger = Logger.getLogger(WxCmdUtil.class);

    public static void sendMsg(String wxId, String text) {
        AppConfigs.wxPushService.sendTextMsg(wxId,text);
    }

}

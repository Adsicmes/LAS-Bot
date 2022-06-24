package com.las.cmd.judge;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.las.cmd.BaseNonCommand;
import com.las.common.Constant;
import com.las.utils.CmdUtil;
import com.las.utils.JsonUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 非匹配指令该类不作为默认的，由开发者用户自己定义，此类作为参考
 */
public class ImgllegalCmd extends BaseNonCommand {

    private static Logger logger = Logger.getLogger(ImgllegalCmd.class);

    @Override
    public void execute(JSONObject obj, Long userId, Long id, Integer type, String command, ArrayList<String> args) throws Exception {
        JSONArray msgChain = obj.getJSONArray("messageChain");
        if (CollectionUtil.isNotEmpty(msgChain)) {
            for (int i = 0; i < msgChain.size(); i++) {
                JSONObject jsonObject = msgChain.getJSONObject(i);
                String msgType = jsonObject.getString("type");
                if ("Image".equals(msgType)) {
                    String url = jsonObject.getString("url");
                    logger.info("内置BOT监听图url:" + url);
                    //sendImageJudge(url, obj, userId, id);
                }
            }
        }

    }

    private void sendImageJudge(String url, JSONObject cqMsg, Long userId, Long id) {
        int start = url.indexOf("//");
        int length = url.substring(0, start).length();
        String preFix = url.substring(0, length);
        String str = url.substring(length + 2);
        str = str.replaceAll("//", "/");
        String finalUrl = preFix + "//" + str;
        logger.info("该图url：" + finalUrl);

        // 后续要用线程池，每次new Thread 开销很大
        new Thread(() -> {
            Map<String, String> info = new HashMap<>();
            info.put("grant_type", "client_credentials");
            info.put("client_id", "TwGSLNbo6kYrOlApnKL5wZmN");
            info.put("client_secret", "ldUZ8h0lG3t49Ripj4URFZIW3TGcyeR4");
            String result = HttpKit.post("https://aip.baidubce.com/oauth/2.0/token", info, null);
            JSONObject object = JsonUtils.getJsonObjectByJsonString(result);
            String token = object.getString("access_token");

            info = new HashMap<>();
            info.put("access_token", token);
            info.put("imgUrl", finalUrl);
            String result2 = HttpKit.post("https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined", info, null);
            logger.info("判定结果：" + result2);
            JSONObject object2 = JsonUtils.getJsonObjectByJsonString(result2);
            String judge = object2.getString("conclusion");
            String msg = "该图判定结果是：" + judge;
            Double v = null;
            String dataMsg = null;
            if ("不合规".equals(judge) || "疑似".equals(judge)) {
                v = object2.getJSONArray("data").getJSONObject(0).getDouble("probability");
                dataMsg = object2.getJSONArray("data").getJSONObject(0).getString("msg");
                String data = String.format("%.2f", v * 100) + "%";
                //logger.info("色情值：" + data);
                msg = msg + "\n违规原因：" + dataMsg;
                msg = msg + "\n鉴定值：" + data;
            }

            logger.info("判定结果是：" + msg);
            logger.info("消息类型：" + cqMsg.getString("type"));

            switch (cqMsg.getString("type")) {
                case "FriendMessage":
                    CmdUtil.sendMessage(msg, userId, id, Constant.MESSAGE_TYPE_PRIVATE);
                    break;
                case "GroupMessage":
                    if ("不合规".equals(judge)) {
                        // 还需要判断鉴定值大于90%的色情色图才发到群里，以免刷屏
                        // (具体数值范围后续读取群配置)
                        if (null != v && v > 0.6) {
                            sendJudge90Msg(msg, dataMsg, userId, id);
                        } else {
                            if (null != v && v > 0.2) {
                                sendJudge20Msg(msg, dataMsg, userId, id);
                            }
                        }
                    } else if ("疑似".equals(judge)) {
                        if (null != v && v > 0.2) {
                            sendJudge20Msg(msg, dataMsg, userId, id);
                        }
                    }
                    break;
                case "TempMessage":
                    CmdUtil.sendMessage(msg, userId, id, Constant.MESSAGE_TYPE_DISCUSS);
                    break;
                default:
                    break;
            }

            // 之后存入数据到数据库，后续做一些分析以及新功能“订阅”
            // 后续再弄
        }).start();
    }

    /**
     * 色情值大于90的判定
     */
    private void sendJudge90Msg(String msg, String dataMsg, Long userId, Long id) {
        if (dataMsg.contains("卡通女性性感") || dataMsg.contains("卡通色情") || dataMsg.contains("女性性感")) {
            msg = msg + "\n\n[注] 不要在群里发太多色图！尽可能私聊发~";
            CmdUtil.sendAtMessage(msg, userId, userId, id, Constant.MESSAGE_TYPE_GROUP);
        }

        if (dataMsg.contains("一般色情")) {
            msg = msg + "\n\n[注] 不要在群里发庸俗的图！要发就私聊发给我~";
            CmdUtil.sendAtMessage(msg, userId, userId, id, Constant.MESSAGE_TYPE_GROUP);
        }
    }


    private void sendJudge20Msg(String msg, String dataMsg, Long userId, Long id) {
        if (dataMsg.contains("卡通色情")) {
            msg = msg + "\n\n[注] 不要在群里发太多色图！尽可能私聊发~";
            CmdUtil.sendAtMessage(msg, userId, userId, id, Constant.MESSAGE_TYPE_GROUP);
        }

        if (dataMsg.contains("一般色情")) {
            msg = msg + "\n\n[注] 不要在群里发庸俗的图！要发就私聊发给我~";
            CmdUtil.sendAtMessage(msg, userId, userId, id, Constant.MESSAGE_TYPE_GROUP);
        }
    }
}

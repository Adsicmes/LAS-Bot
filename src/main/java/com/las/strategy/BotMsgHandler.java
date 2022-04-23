package com.las.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.cmd.Command;
import com.las.common.Constant;
import com.las.dao.GroupDao;
import com.las.dao.UserDao;
import com.las.utils.ClassUtil;
import com.las.utils.CmdUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.las.config.AppConfigs.APP_CONTEXT;

/**
 * 该抽象类的作用就是实现各种处理
 */
public abstract class BotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(BotMsgHandler.class);

    private JSONObject sender;

    private JSONArray msgChain;

    // 消息类型 0表示私有 1群消息 2讨论组
    private int type = -1;

    // 消息内容
    private String msgData;

    private GroupDao groupDao;

    private UserDao userDao;

    public BotMsgHandler() {
        this.groupDao = (GroupDao) APP_CONTEXT.getBean("groupDao");
        this.userDao = (UserDao) APP_CONTEXT.getBean("userDao");
    }

    //后续安装下lombok插件，就不用总是写getter方法了，很累...
    public JSONObject getSender() {
        return sender;
    }

    public JSONArray getMsgChain() {
        return msgChain;
    }

    public int getMsgType() {
        return type;
    }

    public String getMsgData() {
        return msgData;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }


    /**
     * 实现接口的执行消息方法(子类也可以去重新实现)
     */
    @Override
    public void exec() {
        logger.info("bot开始执行默认消息...");
        //此方法可以由子类重写方法去做对应的事件
    }

    /**
     * 实现接口的处理消息方法(子类也可以去重新实现)
     */
    @Override
    public void handleMsg(Map map) {
        logger.info("bot开始处理消息...");
        JSONObject object = JSON.parseObject(JSONObject.toJSONString(map));
        sender = handleSender(object);
        msgChain = handleMsgChain(object);
        type = handleMsgType(object);
        handleMsgData();
    }

    /**
     * 定义处理sender消息的方法
     */
    private JSONObject handleSender(JSONObject jsonObject) {
        return jsonObject.getJSONObject("sender");
    }

    /**
     * 定义处理messageChain消息的方法
     */
    private JSONArray handleMsgChain(JSONObject jsonObject) {
        return jsonObject.getJSONArray("messageChain");
    }

    /**
     * 定义处理消息类型的方法
     */
    private int handleMsgType(JSONObject jsonObject) {
        String type = jsonObject.getString("type");
        switch (type) {
            case "FriendMessage":
                return Constant.MESSAGE_TYPE_PRIVATE;
            case "GroupMessage":
                return Constant.MESSAGE_TYPE_GROUP;
            case "TempMessage":
                return Constant.MESSAGE_TYPE_DISCUSS;
            default:
                return -1;
        }
    }

    /**
     * 定义处理消息内容的方法
     */
    private void handleMsgData() {
        for (int i = 0; i < msgChain.size(); i++) {
            JSONObject jsonObj = msgChain.getJSONObject(i);
            if ("Plain".equals(jsonObj.getString("type"))) {
                msgData = jsonObj.getString("text");
                break;
            }
        }
    }

    /**
     * 定义处理获取指令并且执行的方法
     */
    protected Command exeCommand() {
        Command command = null;
        String msgData = getMsgData();
        if (StrKit.isBlank(msgData)) {
            return null;
        }
        String cmd = CmdUtil.getLowerParams(msgData);
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper("com", false, Command.class);
        for (Class c : classSet) {
            if (null != command) {
                logger.info("指令类是：" + command.toString());
                break;
            }
            Class superclass = c.getSuperclass();
            Field[] fields = superclass.getDeclaredFields();
            List<String> cmdList = new ArrayList<>();
            for (Field field : fields) {
                String colName = field.getName();
                String methodName = "get" + colName.substring(0, 1).toUpperCase() + colName.substring(1);
                Method method;
                Object o = null;
                try {
                    method = superclass.getDeclaredMethod(methodName);
                    o = method.invoke(c.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ("alias".equalsIgnoreCase(colName)) {
                    if (cmdList.isEmpty()) {
                        cmdList = (List<String>) o;
                    } else {
                        cmdList.addAll((List<String>) o);
                    }
                }
                if ("name".equalsIgnoreCase(colName)) {
                    cmdList.add(o.toString());
                }
            }
            if (StrKit.notBlank(cmd)) {
                int cmdLength = 0;
                for (String cmds : cmdList) {
                    if (StrKit.notBlank(cmds) && (cmd.toUpperCase().startsWith(cmds) || cmd.toLowerCase().startsWith(cmds))) {
                        if (cmdLength < cmds.length()) {
                            cmdLength = cmds.length();
                            if (null == command) {
                                try {
                                    command = (Command) c.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (null != command) {
            logger.info("确认指令类是：" + command.toString());
            Long userId = getSender().getLong("id");
            JSONObject group = getSender().getJSONObject("group");
            Long id;
            if (null != group) {
                id = group.getLong("id");
            } else {
                id = userId;
            }
            command.execute(userId, id, getMsgType(), cmd, CmdUtil.getParamsArray(cmd));
        }

        return command;
    }

}
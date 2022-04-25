package com.las.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.dao.GroupDao;
import com.las.dao.UserDao;
import com.las.model.Group;
import com.las.model.User;
import com.las.utils.*;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // 用户ID
    private Long userId;

    // 组ID
    private Long id;

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

    public Long getUserId() {
        return userId;
    }

    public Long getId() {
        return id;
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
     * 实现接口的处理消息方法(子类不可以去重新实现)
     */
    @Override
    public final void handleMsg(Map map) {
        JSONObject object = JSON.parseObject(JSONObject.toJSONString(map));
        sender = object.getJSONObject("sender");
        msgChain = object.getJSONArray("messageChain");
        String strType = object.getString("type");
        switch (strType) {
            case "FriendMessage":
                type = Constant.MESSAGE_TYPE_PRIVATE;
                break;
            case "GroupMessage":
                type = Constant.MESSAGE_TYPE_GROUP;
                break;
            case "TempMessage":
                type = Constant.MESSAGE_TYPE_DISCUSS;
                break;
            default:
                break;
        }
        if (CollectionUtil.isNotEmpty(msgChain)) {
            for (int i = 0; i < msgChain.size(); i++) {
                JSONObject jsonObj = msgChain.getJSONObject(i);
                if ("Plain".equals(jsonObj.getString("type"))) {
                    msgData = jsonObj.getString("text");
                    break;
                }
            }
        }
        userId = getSender().getLong("id");
        JSONObject group = getSender().getJSONObject("group");
        if (null != group) {
            id = group.getLong("id");
        } else {
            id = userId;
        }
    }


    /**
     * 实现接口的执行指令方法(子类不可以去重新实现)
     */
    @Override
    public final void exeCommand(String msg, Long userId, Long id, int type) {
        Command command = null;
        int cmdLength = 0;
        if (StrKit.isBlank(msg)) {
            return;
        }
        if (msg.startsWith(Constant.DEFAULT_PRE)) {
            msg = msg.substring(1);
        }
        String cmd = CmdUtil.getLowerParams(msg);
        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, BotCmd.class);
        for (Class c : classSet) {
            if (null != command) {
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
            logger.info("执行指令是：" + command.toString());
            command.execute(userId, id, type, cmd, CmdUtil.getParamsArray(CmdUtil.getParams(cmd, cmdLength)));
        }

    }

    /**
     * 初始化机器人好友和群
     */
    protected void initBot() {
        List<JSONObject> list = MiraiUtil.getInstance().getGroupList();
        //采取异步
        list.parallelStream().forEach(item -> {
            Group group = getGroupDao().findByGid(item.getLong("id"));
            if (null == group) {
                group = new Group();
            }
            //id存在则是做更新
            group.setName(item.getString("name"));
            group.setGroupId(item.getLong("id"));
            group.setGroupRole(item.getString("permission"));
            group.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
            getGroupDao().saveOrUpdate(group);
        });

        //下一步查询机器人QQ所有的好友列表
        List<JSONObject> friendList = MiraiUtil.getInstance().getFriendList();
        friendList.parallelStream().forEach(item -> {
            logger.info(item);
            User user = getUserDao().findByUid(item.getLong("id"));
            if (null == user) {
                user = new User();
            }
            user.setUserId(item.getLong("id"));
            user.setNickname(EmojiUtil.emojiChange(item.getString("nickname")));
            user.setRemark(EmojiUtil.emojiChange(item.getString("remark")));
            user.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
            if (null == user.getFunPermission()) {
                //说明该用户是第一次？默认设置权限0
                user.setFunPermission(Constant.DEFAULT_PERMISSION);
            }
            getUserDao().saveOrUpdate(user);
        });
    }


}
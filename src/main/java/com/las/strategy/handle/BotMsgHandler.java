package com.las.strategy.handle;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.dao.FunDao;
import com.las.dao.GroupDao;
import com.las.dao.GroupFunDao;
import com.las.dao.UserDao;
import com.las.model.Fun;
import com.las.model.Group;
import com.las.model.GroupFun;
import com.las.model.User;
import com.las.strategy.BotStrategy;
import com.las.utils.*;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.las.config.AppConfigs.APP_CONTEXT;

/**
 * 该抽象类的作用就是实现各种处理
 */
public abstract class BotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(BotMsgHandler.class);

    // 消息完整对象
    private JSONObject object;
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

    private FunDao funDao;

    private GroupFunDao groupFunDao;

    protected BotMsgHandler() {
        this.groupDao = (GroupDao) APP_CONTEXT.getBean("groupDao");
        this.userDao = (UserDao) APP_CONTEXT.getBean("userDao");
        this.funDao = (FunDao) APP_CONTEXT.getBean("funDao");
        this.groupFunDao = (GroupFunDao) APP_CONTEXT.getBean("groupFunDao");
    }


    //不推荐安装lombok插件，对应需要的方法才弄getter... 并且权限使用的是缺省

    int getMsgType() {
        return type;
    }

    String getMsgData() {
        return msgData;
    }

    Long getUserId() {
        return userId;
    }

    Long getId() {
        return id;
    }

    JSONObject getMsgObject() {
        return object;
    }

    // Dao可以公开给其他类使用

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public FunDao getFunDao() {
        return funDao;
    }

    public GroupFunDao getGroupFunDao() {
        return groupFunDao;
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
        object = JSON.parseObject(JSONObject.toJSONString(map));
        JSONObject sender = object.getJSONObject("sender");
        JSONArray msgChain = object.getJSONArray("messageChain");
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
        if (null != sender) {
            userId = sender.getLong("id");
            JSONObject group = sender.getJSONObject("group");
            if (null != group) {
                id = group.getLong("id");
            } else {
                id = userId;
            }
        }
    }


    /**
     * 执行指令方法(权限为缺省：不同包的类不可以去使用)
     */
    void exeCommand(String msg, Long userId, Long id, int type) {
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
            try {
                command.execute(userId, id, type, cmd, CmdUtil.getParamsArray(CmdUtil.getParams(cmd, cmdLength)));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(super.toString() + "执行时报错，命令内容:" + cmd);
            }
        }

    }

    /**
     * 初始化机器人权限(权限为保护：只允许子类去使用)
     */
    protected void initBotFun() {
        // 下一步查询所有指令注解上的功能名字和权限数值插入到数据库里
        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, BotCmd.class);
        classSet.forEach(aClass -> {
            BotCmd botCmd = aClass.getDeclaredAnnotation(BotCmd.class);
            String funName = botCmd.funName();
            int funWeight = botCmd.funWeight();
            List<Fun> funList = getFunDao().findAll();
            Fun fun = Optional.ofNullable(funList).orElse(new ArrayList<>()).stream()
                    .filter(funObj -> funName.equals(funObj.getFunName()) && Long.parseLong(AppConfigs.BOT_QQ) == funObj.getBotQQ())
                    .findFirst()
                    .orElseGet(Fun::new);
            fun.setFunName(funName);
            fun.setFunweight(funWeight);
            fun.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
            getFunDao().saveOrUpdate(fun);
        });

        // 最后一步，检查群功能数据
        List<Group> groupList = getGroupDao().findAll();
        groupList.forEach(group -> {
            Long groupId = group.getGroupId();
            List<GroupFun> groupFunList = getGroupFunDao().findListByGid(groupId);
            List<Fun> funList = getFunDao().findAll();
            funList.forEach(fun -> {
                for (GroupFun groupFun : groupFunList) {
                    // 找到之前存在相同的功能名，则跳过
                    if (groupFun.getGroupFun().equals(fun.getFunName())) {
                        return;
                    }
                }
                GroupFun groupFun = new GroupFun();
                groupFun.setGroupFun(fun.getFunName());
                groupFun.setGroupId(groupId);
                groupFun.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
                groupFun.setIsEnable(1);
                getGroupFunDao().saveOrUpdate(groupFun);
            });
        });
    }

    /**
     * 初始化机器人好友和群(权限为保护：只允许子类去使用)
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

        // 顺便初始化机器人权限
        initBotFun();

    }


}
package com.las.strategy.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.cmd.Command;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.dao.*;
import com.las.model.*;
import com.las.strategy.BotStrategy;
import com.las.utils.*;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private JSONObject sender;

    private JSONArray msgChain;

    private GroupDao groupDao;

    private UserDao userDao;

    private FunDao funDao;

    private GroupFunDao groupFunDao;

    private GroupExtDao groupExtDao;

    protected BotMsgHandler() {
        this.groupDao = (GroupDao) APP_CONTEXT.getBean("groupDao");
        this.userDao = (UserDao) APP_CONTEXT.getBean("userDao");
        this.funDao = (FunDao) APP_CONTEXT.getBean("funDao");
        this.groupFunDao = (GroupFunDao) APP_CONTEXT.getBean("groupFunDao");
        this.groupExtDao = (GroupExtDao) APP_CONTEXT.getBean("groupExtDao");
    }


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

    JSONObject getSender() {
        return sender;
    }

    JSONArray getMsgChain() {
        return msgChain;
    }

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

    public GroupExtDao getGroupExtDao() {
        return groupExtDao;
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
        String cmd = null;
        int cmdLength = 0;
        // 需要找匹配指令的
        if (!StrKit.isBlank(msg)) {
            // 读群配置，看是否根据群需要带
            if (type == Constant.MESSAGE_TYPE_GROUP) {
                GroupExt groupExt = getGroupExtDao().findByGid(id);
                if (!StrUtil.isBlank(groupExt.getAttribute2())
                        && msg.startsWith(groupExt.getAttribute2())) {
                    msg = msg.substring(groupExt.getAttribute2().length());
                } else {
                    if (null == groupExt.getAttribute2()) {
                        // 为空，说明群前缀未设置，不允许触发后面指令方法
                        return;
                    } else if ("".equals(groupExt.getAttribute2())) {
                        if (msg.startsWith(Constant.DEFAULT_PRE)) {
                            msg = msg.substring(1);
                        }
                    }
                }
            } else {
                if (msg.startsWith(Constant.DEFAULT_PRE)) {
                    msg = msg.substring(1);
                }
            }
            cmd = getLowerParams(msg);
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, BotCmd.class);
            for (Class c : classSet) {
                if (null != command) {
                    // 把非匹配指令的跳过
                    BotCmd botCmd = command.getClass().getDeclaredAnnotation(BotCmd.class);
                    if (null != botCmd) {
                        if (botCmd.isMatch()) {
                            break;
                        } else {
                            //匹配到非指令的，需要重新查找指令的
                            //logger.info("匹配非指令：" + command.toString());
                            command = null;
                        }
                    }
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
                boolean isExecute = true;
                // 找到指令之后，查找群是否开启，以及用户权限等问题
                BotCmd botCmd = command.getClass().getDeclaredAnnotation(BotCmd.class);
                if (null != botCmd) {
                    isExecute = checkExe(userId, id, type, botCmd);
                }
                if (isExecute) {
                    try {
                        command.execute(userId, id, type, cmd, getParamsArray(getParams(cmd, cmdLength)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(super.toString() + "执行时报错，命令内容:" + cmd);
                    }
                }
            }
        }

        // 需要查找非匹配指令的
        Set<Class<?>> notCmdSet = ClassUtil.scanPackageByAnnotation("com", false, BotCmd.class);
        for (Class<?> aClass : notCmdSet) {
            BotCmd annotation = aClass.getDeclaredAnnotation(BotCmd.class);
            if (null != annotation && !annotation.isMatch()) {
                // 只要找到一个非指令的，直接执行。并且检查好权限
                boolean isExecute = checkExe(userId, id, type, annotation);
                if (isExecute) {
                    try {
                        Command notCommand = (Command) aClass.newInstance();
                        logger.info("执行非匹配指令是：" + notCommand.toString());
                        notCommand.execute(object, userId, id, type, cmd, getParamsArray(getParams(cmd, cmdLength)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(super.toString() + "执行时报错，非指令命令内容:" + cmd);
                    }
                }
            }
        }


    }

    /**
     * 检查机器人权限是否可执行
     */
    private boolean checkExe(Long userId, Long id, int type, BotCmd botCmd) {
        boolean isExecute = true;
        String funName = botCmd.funName();
        if (type == Constant.MESSAGE_TYPE_GROUP) {
            List<GroupFun> groupFuns = getGroupFunDao().findListByGid(id);
            List<GroupFun> collect = groupFuns.stream().filter(groupFun -> funName.equals(groupFun.getGroupFun()) && groupFun.getIsEnable() == 1).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(collect)) {
                isExecute = false;
                // 说明没找到此功能有启动的数据，返回错误信息（非匹配指令不需要）
                if (botCmd.isMatch()) {
                    CmdUtil.sendMessage("此群未开启该功能：" + funName + "，请联系管理员开启", userId, id, type);
                }
            }
        }
        if (isExecute) {
            // 下一步查询用户权限，用户可能是临时会话和群员，User表没有需要实施插入，并且备注
            int funWeight = botCmd.funWeight();
            User user = getUserDao().findByUid(userId);
            if (null == user) {
                // 用户是来自群or会话消息
                user = new User();
                user.setUserId(userId);
                user.setRemark("来自群或会话组[" + id + "]");
            }
            user.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
            if (null == user.getFunPermission()) {
                //说明该用户是第一次？默认设置权限0
                user.setFunPermission(Constant.DEFAULT_PERMISSION);
            }
            if (null == user.getUsedCount()) {
                // 进来此方法就是有使用过一次
                user.setUsedCount(1);
            } else {
                user.setUsedCount(user.getUsedCount() + 1);
            }
            getUserDao().saveOrUpdate(user);
            if (user.getFunPermission() < funWeight) {
                isExecute = false;
                // 用户权限小于功能权限，则返回错误信息（非匹配指令不需要）
                if (botCmd.isMatch()) {
                    CmdUtil.sendMessage("用户：" + userId + " 权限不足，请联系管理员", userId, id, type);
                }
            }
        }
        return isExecute;
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
                GroupFun groupFun = new GroupFun();
                groupFun.setGroupFun(fun.getFunName());
                groupFun.setGroupId(groupId);
                groupFun.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
                groupFun.setIsEnable(1);
                for (GroupFun gf : groupFunList) {
                    // 找到之前存在相同的功能名，则跳过
                    if (gf.getGroupFun().equals(fun.getFunName())) {
                        return;
                    }
                }
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

            GroupExt groupExt = getGroupExtDao().findByGid(item.getLong("id"));
            if (null == groupExt) {
                groupExt = new GroupExt();
                groupExt.setAttribute1("欢迎小可爱进群>.<");
                groupExt.setAttribute2("#");
            }
            groupExt.setGroupId(item.getLong("id"));
            groupExt.setBotQQ(Long.parseLong(AppConfigs.BOT_QQ));
            getGroupExtDao().saveOrUpdate(groupExt);

        });

        //下一步查询机器人QQ所有的好友列表
        List<JSONObject> friendList = MiraiUtil.getInstance().getFriendList();
        friendList.parallelStream().forEach(item -> {
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
            if (null == user.getUsedCount()) {
                user.setUsedCount(0);
            }
            getUserDao().saveOrUpdate(user);
        });

        // 顺便初始化机器人权限
        initBotFun();

    }

    /**
     * 截取命令后面的参数，例如 点歌 空山新雨后
     *
     * @param cmd 命令（带参数）
     * @param num 截取命令前面长度
     * @return 截取返回 空山新雨后
     */
    private String getParams(String cmd, int num) {
        String param = "";
        if (StrUtils.isNotBlank(cmd)) {
            param = cmd.substring(num).trim();
        }
        return param;
    }


    /**
     * 将命令后面的参数返回一个参数集合
     *
     * @param params 参数（例如 1 100）
     * @return 返回 list
     */
    private ArrayList<String> getParamsArray(String params) {
        ArrayList<String> list = new ArrayList<>();
        if (StrKit.notBlank(params)) {
            String[] split = params.split(" ");
            if (split.length > 0) {
                Collections.addAll(list, split);
            }
        }
        return list;
    }

    /**
     * 参数中多个空格转换为一个空格（并且改为小写）
     *
     * @param msg 参数消息
     * @return 优化好的参数
     */
    private String getLowerParams(String msg) {
        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(msg);
        return m.replaceAll(" ").toLowerCase().trim();
    }


}
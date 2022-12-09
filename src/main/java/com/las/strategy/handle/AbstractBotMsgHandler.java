package com.las.strategy.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.las.annotation.BotCmd;
import com.las.cmd.BaseCommand;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.dao.*;
import com.las.model.*;
import com.las.strategy.BotStrategy;
import com.las.utils.*;
import com.las.utils.mirai.CmdUtil;
import com.las.utils.mirai.MiRaiUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * 该抽象类的作用就是实现各种处理
 *
 * @author dullwolf
 */
public abstract class AbstractBotMsgHandler implements BotStrategy {

    private static Logger logger = Logger.getLogger(AbstractBotMsgHandler.class);

    /**
     * QQ消息完整对象
     */
    private JSONObject cqObj;

    /**
     * 消息类型 0表示私有 1群消息 2讨论组
     */
    private int type;

    /**
     * 消息内容
     */
    private String msgData;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 组ID
     */
    private Long id;

    // 下面是dao，使用SpringIOC，提升性能
    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FunDao funDao;

    @Autowired
    private GroupFunDao groupFunDao;

    @Autowired
    private GroupExtDao groupExtDao;

//    protected AbstractBotMsgHandler() {
//        this.groupDao = new GroupDao();
//        this.userDao = new UserDao();
//        this.funDao = new FunDao();
//        this.groupFunDao = new GroupFunDao();
//        this.groupExtDao = new GroupExtDao();
//    }

    /**
     * 提供getter
     */
    protected JSONObject getCqObj() {
        return cqObj;
    }

    protected int getType() {
        return type;
    }

    protected String getMsgData() {
        return msgData;
    }

    protected Long getUserId() {
        return userId;
    }

    protected Long getId() {
        return id;
    }

    protected GroupDao getGroupDao() {
        return groupDao;
    }

    protected UserDao getUserDao() {
        return userDao;
    }

    protected FunDao getFunDao() {
        return funDao;
    }

    protected GroupFunDao getGroupFunDao() {
        return groupFunDao;
    }

    protected GroupExtDao getGroupExtDao() {
        return groupExtDao;
    }


    /**
     * 实现接口的处理消息方法(子类不可以去重新实现)
     */
    @Override
    public final void handleMsg(Map map) {
        cqObj = JSON.parseObject(JSONObject.toJSONString(map));
        JSONObject sender = cqObj.getJSONObject("sender");
        JSONArray msgChain = cqObj.getJSONArray("messageChain");
        String strType = cqObj.getString("type");
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
     * 执行指令方法
     */
    protected void exeCommand(String msg, Long userId, Long id, int type) {
        try {
            List<BaseCommand> commands = new ArrayList<>();
            BaseCommand command = null;
            String cmd = getLowerParams(msg);
            int cmdLength = 0;
            // 需要查找非匹配指令的（优先非匹配指令的）
            Set<Class<?>> notCmdSet = ClassUtil.scanPackageByAnnotation("com", false, BotCmd.class);
            for (Class<?> aClass : notCmdSet) {
                BotCmd annotation = aClass.getDeclaredAnnotation(BotCmd.class);
                if (null != annotation && !annotation.isMatch()) {
                    // 只要找到一个非指令的，直接执行。并且检查好权限
                    boolean isExecute = checkExe(userId, id, type, annotation);
                    if (isExecute) {
                        try {
                            String simpleName = aClass.getSimpleName();
                            String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                            Object obj = SpringUtils.getBean(beanName);
                            BaseCommand notCommand = (BaseCommand) obj;
                            logger.info("执行非匹配指令是：" + notCommand.toString());
                            notCommand.execute(cqObj, userId, id, type, cmd, getParamsArray(getParams(cmd, cmdLength)));
                        } catch (Exception e) {
                            logger.error(super.toString() + "执行时报错，非指令命令内容:" + cmd);
                        }
                    }
                }
            }
            if (StrKit.isBlank(msg)) {
                logger.error("执行指令传入msg为空！");
                return;
            }
            // 需要找匹配指令的
            msg = readPrefix(msg, id, type);
            if (msg == null) {
                return;
            }
            cmd = getLowerParams(msg);
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com", false, BotCmd.class);
            for (Class<?> c : classSet) {
                if (null != command) {
                    // 把非匹配指令的跳过
                    BotCmd botCmd = command.getClass().getDeclaredAnnotation(BotCmd.class);
                    if (null != botCmd) {
                        if (botCmd.isMatch()) {
                            break;
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
                        String simpleName = c.getSimpleName();
                        String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                        Object obj = SpringUtils.getBean(beanName);
                        o = method.invoke(obj);
                    } catch (Exception e) {
                        logger.error("出错ERROR：" + e.getMessage(), e);
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
                    for (String oneCmd : cmdList) {
                        if (!StrKit.isBlank(oneCmd) && (cmd.toUpperCase().startsWith(oneCmd) || cmd.toLowerCase().startsWith(oneCmd))) {
                            if (cmdLength < oneCmd.length()) {
                                cmdLength = oneCmd.length();
                                if (null == command) {
                                    try {
                                        String simpleName = c.getSimpleName();
                                        String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                                        Object obj = SpringUtils.getBean(beanName);
                                        command = (BaseCommand) obj;
                                        commands.add(command);
                                    } catch (Exception e) {
                                        logger.error("出错ERROR：" + e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(commands)) {
                // 因为可能有开发者用户覆盖内置机器人点歌功能，可以按顺序排优先级高的
                Optional<BaseCommand> baseCommand = commands.stream().max(Comparator.comparing(BaseCommand::getPriority));
                if (baseCommand.isPresent()) {
                    BaseCommand finalBaseCommand = baseCommand.get();
                    logger.info("最终执行指令是：" + finalBaseCommand.toString());
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
                            logger.error(super.toString() + "执行时报错，命令内容:" + cmd);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("消息处理命令时报错：" + e.toString(), e);
        }


    }

    /**
     * 读群配置，看是否根据群需要带前缀
     */
    private String readPrefix(String msg, Long id, int type) {
        if (type == Constant.MESSAGE_TYPE_GROUP) {
            GroupExt groupExt = getGroupExtDao().findByGid(id);
            if (ObjectUtil.isNotNull(groupExt)) {
                if (!StrUtil.isBlank(groupExt.getAttribute2())
                        && msg.startsWith(groupExt.getAttribute2())) {
                    msg = msg.substring(groupExt.getAttribute2().length());
                } else {
                    // 这后面的逻辑有些复杂，细细品味，后面看总结注释 ↓
                    if (null == groupExt.getAttribute2()) {
                        // 为空，说明群前缀未设置，不允许触发后面指令方法
                        return null;
                    } else if ("".equals(groupExt.getAttribute2())) {
                        if (msg.startsWith(Constant.DEFAULT_PRE)) {
                            msg = msg.substring(1);
                        }
                    } else {
                        // 设置了其他的前缀符号，需要判断是不是
                        if (msg.startsWith(groupExt.getAttribute2())) {
                            msg = msg.substring(1);
                        } else {
                            // 否则也不允许触发后面的指令方法
                            return null;
                        }
                    }
                    // 总结：运行情况如下
                    // 1、群配置没有设置（一般不可能，机器人初始化默认会在扩展属性2设置#）
                    // 2、设置了除开#之外的符号，例如设置-符号，在群里必须使用-符号
                    // 3、设置空字符串，说明可以带#也可以不带，两种均可触发指令
                }
            }
        } else {
            if (msg.startsWith(Constant.DEFAULT_PRE)) {
                msg = msg.substring(1);
            }
        }
        return msg;
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
                    logger.warn("该群：" + id + "，未开启功能：" + funName + "，请联系管理员开启");
                    CmdUtil.sendMessage("该群：" + id + "，未开启功能：" + funName + "，请联系管理员开启", userId, id, type);

                }
            }
        }
        User user = null;
        if (isExecute) {
            // 下一步查询用户权限，用户可能是临时会话和群员，User表没有需要实施插入，并且备注
            int funWeight = botCmd.funWeight();
            user = getUserDao().findByUid(userId);
            if (null == user) {
                // 用户是来自群or会话消息
                user = new User();
                user.setUserId(userId);
                user.setRemark("来自群或会话组[" + id + "]");
            }
            user.setBotQQ(Long.parseLong(AppConfigs.botQQ));
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
            if (AppConfigs.superQQ.equals(userId.toString())) {
                user.setFunPermission(Constant.SUPER_PERMISSION);
            } else {
                if (funWeight < Constant.ADMIN_PERMISSION) {
                    if (user.getFunPermission() < funWeight) {
                        isExecute = false;
                        // 用户权限小于功能权限，则返回错误信息（非匹配指令不需要）
                        if (botCmd.isMatch()) {
                            logger.warn("用户：" + userId + " 权限不足，请联系管理员");
                            CmdUtil.sendMessage("用户：" + userId + " 权限不足，请联系管理员", userId, id, type);
                        }
                    }
                } else {
                    // 管理员的功能需要查找管理员初始用户
                    User groupUser = getUserDao().findGroupUser(userId, id);
                    if (null == groupUser || groupUser.getFunPermission() < funWeight) {
                        isExecute = false;
                        // 用户权限小于功能权限，则返回错误信息（非匹配指令不需要）
                        if (botCmd.isMatch()) {
                            logger.warn("群管：" + userId + " 权限不足，请联系超管");
                            CmdUtil.sendMessage("群管：" + userId + " 权限不足，请联系超管", userId, id, type);
                        }
                    }
                }
            }
        }
        if (isExecute && botCmd.isMatch()) {
            getUserDao().saveOrUpdate(user);
        }
        return isExecute;
    }


    /**
     * 初始化机器人好友和群(权限为保护：只允许子类去使用)
     */
    protected void initBot() {
        String result = MiRaiUtil.getInstance().getGroupList();
        List<JSONObject> list = new ArrayList<>();
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
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
            group.setBotQQ(Long.parseLong(AppConfigs.botQQ));
            getGroupDao().saveOrUpdate(group);

            GroupExt groupExt = getGroupExtDao().findByGid(item.getLong("id"));
            if (null == groupExt) {
                groupExt = new GroupExt();
                groupExt.setAttribute1("欢迎小可爱进群>.<");
                groupExt.setAttribute2("#");
            }
            groupExt.setGroupId(item.getLong("id"));
            groupExt.setBotQQ(Long.parseLong(AppConfigs.botQQ));
            getGroupExtDao().saveOrUpdate(groupExt);

        });

        //下一步查询机器人QQ所有的好友列表
        String friendMsg = MiRaiUtil.getInstance().getFriendList();
        List<JSONObject> friendList = new ArrayList<>();
        if (CollectionUtil.isEmpty(friendList)) {
            return;
        }
        friendList.parallelStream().forEach(item -> {
            User user = getUserDao().findByUid(item.getLong("id"));
            if (null == user) {
                user = new User();
            }
            user.setUserId(item.getLong("id"));
            user.setNickname(EmojiUtil.emojiChange(item.getString("nickname")));
            user.setRemark(EmojiUtil.emojiChange(item.getString("remark")));
            user.setBotQQ(Long.parseLong(AppConfigs.botQQ));
            if (null == user.getFunPermission()) {
                //说明该用户是第一次？默认设置权限0
                user.setFunPermission(Constant.DEFAULT_PERMISSION);
            }
            if (null == user.getUsedCount()) {
                user.setUsedCount(0);
            }
            getUserDao().saveOrUpdate(user);
        });
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
                    .filter(funObj -> funName.equals(funObj.getFunName()) && Long.parseLong(AppConfigs.botQQ) == funObj.getBotQQ())
                    .findFirst()
                    .orElseGet(Fun::new);
            fun.setFunName(funName);
            fun.setFunweight(funWeight);
            fun.setBotQQ(Long.parseLong(AppConfigs.botQQ));
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
                groupFun.setBotQQ(Long.parseLong(AppConfigs.botQQ));
                groupFun.setIsEnable(1);
                for (GroupFun gf : groupFunList) {
                    // 找到之前存在相同的功能名，则跳过
                    if (gf.getGroupFun().equals(fun.getFunName())) {
                        return;
                    }
                }
                getGroupFunDao().saveOrUpdate(groupFun);
            });
            //还需要把之前旧的FUN需要清空
            List<String> newFunNames = funList.stream().map(Fun::getFunName).collect(Collectors.toList());
            groupFunList.forEach(groupFun -> {
                if (!newFunNames.contains(groupFun.getGroupFun())) {
                    //说明当前这个群功能是旧的，需要删除
                    getGroupFunDao().deleteById(groupFun.getId());
                }
            });
        });
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
        if (StrUtils.isBlank(msg)) {
            return null;
        }
        Matcher m = Constant.PATTERN_ONE_SPACE.matcher(msg);
        return m.replaceAll(" ").toLowerCase().trim();
    }


}
package com.las.strategy.handle;

import com.alibaba.fastjson.JSONObject;
import com.las.common.Constant;
import com.las.dao.GroupDao;
import com.las.model.Group;
import com.las.model.User;
import com.las.strategy.BotMsgHandler;
import com.las.utils.EmojiUtil;
import com.las.utils.MiraiUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class BotOnlineMsgHandler extends BotMsgHandler {

    private static Logger logger = Logger.getLogger(BotOnlineMsgHandler.class);

    @Override
    public void exec() {
        List<JSONObject> list = MiraiUtil.getInstance().getGroupList();
        //采取异步
        list.parallelStream().forEach(item -> {
            Group group = getGroupDao().findByGid(item.getLong("id"));
            if(null == group){
                group = new Group();
            }
            //id存在则是做更新
            group.setName(item.getString("name"));
            group.setGroupId(item.getLong("id"));
            group.setGroupRole(item.getString("permission"));
            getGroupDao().saveOrUpdate(group);
        });

        //下一步查询机器人QQ所有的好友列表
        List<JSONObject> friendList = MiraiUtil.getInstance().getFriendList();
        friendList.parallelStream().forEach(item -> {
            logger.info(item);
            User user = getUserDao().findByUid(item.getLong("id"));
            if(null == user){
                user = new User();
            }
            user.setUserId(item.getLong("id"));
            user.setNickname(EmojiUtil.emojiChange(item.getString("nickname")));
            user.setRemark(item.getString("remark"));
            if (null == user.getFunPermission()) {
                //说明该用户是第一次？默认设置权限0
                user.setFunPermission(Constant.DEFAULT_PERMISSION);
            }
            getUserDao().saveOrUpdate(user);
        });


    }
}

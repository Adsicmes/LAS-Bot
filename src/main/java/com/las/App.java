package com.las;

import com.las.dao.TestDao;
import org.apache.log4j.Logger;

public class App {

    private static Logger logger = Logger.getLogger(App.class);

    private static String qqGroupFunctions(long qqGroup) {
        //目前都暂时全部用静态，后续需要重新改造，不允许使用static
        return TestDao.query(qqGroup);
    }


    public static void main(String[] args) {
        //sign(1483492332, "sdfw");
        String info = qqGroupFunctions(1483492332L);
        logger.info("测试查询群功能信息：" + info);
    }
}
import config.AppConfigs;
import dao.TestDao;
import org.apache.log4j.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    public static String qqGroupFunctions(long qqGroup) {
        //目前都暂时全部用静态，后续需要重新改造，不允许使用static
        return TestDao.query(qqGroup);
    }


    public static void main(String[] args) {
        //sign(1483492332, "sdfw");
        String info = qqGroupFunctions(1483492332L);
        logger.info("测试查询群功能信息：" + info);
    }
}
package com.las;

import com.las.netty.EchoServer;
import org.apache.log4j.Logger;

public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        try {
            logger.info("准备初始化bot...");
            new EchoServer(8888).start(); // 启动netty
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
package com.las.core;/*
 * 该文件用于收取来自mirai的信息并转化为json发送给Filter进行消息过滤
 * */

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(value = "/bot",loadOnStartup = 1) //注册路径
public class MessageReceiver extends HttpServlet {  //继承Servlet类

    private static Logger logger = Logger.getLogger(MessageReceiver.class);

    @Override
    public void init() throws ServletException {
        logger.debug("初始化bot...");
    }

    @Override  //重写
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");  //设置字符集
        resp.setContentType("html/text;charset=UTF-8");

        ServletInputStream sis = req.getInputStream();  //获取mirai输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(sis));  //buffer读取输入流到字符串
        String line = br.readLine();  //解析读取每条消息到json

        //System.out.println(line);
        logger.debug(line);
    }
}
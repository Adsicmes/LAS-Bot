/*
* 该文件用于收取来自mirai的信息并转化为json发送给Filter进行消息过滤
* */

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/bot") //注册路径
public class MessageReceiver extends HttpServlet {  //继承Servlet类
    @Override  //重写
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");  //设置字符集
        resp.setContentType("html/text;charset=UTF-8");

        ServletInputStream sis = req.getInputStream();  //获取mirai输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(sis));  //buffer读取输入流到字符串
        String line = br.readLine();  //解析读取每条消息到json

        System.out.println(line);
    }
}

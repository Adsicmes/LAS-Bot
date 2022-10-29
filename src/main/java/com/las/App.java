package com.las;

import com.las.annotation.EnableCQ;
import com.las.annotation.EnableMirai;
import com.las.core.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动入口
 *
 * @author dullwolf
 */
@EnableMirai(superQQ = "1091569752", botQQ = "2547170055", keyAuth = "dw0123456789", isEnableWxBot = true)
@EnableCQ
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        Bot.run(App.class);
    }
}
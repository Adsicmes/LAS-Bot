package com.las;

import com.las.annotation.EnableCQ;
import com.las.annotation.EnableMirai;
import com.las.core.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMirai(superQQ = "2547170055", botQQ = "1091569752", keyAuth = "123456")
@EnableCQ
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        Bot.run(App.class);
    }
}

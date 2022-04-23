package com.las;

import com.las.annotation.BotRun;

@BotRun(qq = "2547170055",
        qqAuth = "dw0123456789",
        miraiUrl = "http://localhost:5700",
        botServer = "/cq/getMsg",
        botPort = 8080)
public class App {

    public static void main(String[] args) {
        LASBot.run(App.class);
    }

}
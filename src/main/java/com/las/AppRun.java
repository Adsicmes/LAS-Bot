package com.las;

import com.las.annotation.BotRun;

@BotRun(superQQ = "1091569752", botQQ = "2547170055", keyAuth = "dw0123456789",
        miraiUrl = "http://localhost:5700",
        botServer = "/cq/getMsg",
        botPort = 8080)
public class AppRun {

    public static void main(String[] args) {
        LASBot.run(AppRun.class);
    }

}

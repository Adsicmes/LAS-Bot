package com.las;

import com.las.annotation.BotRun;

@BotRun(miraiUrl = "http://localhost:5700", botServer = "/cq/getMsg", botPort = 8080)
public class App {

    public static void main(String[] args) {
        LASBot.run(App.class);
    }

}
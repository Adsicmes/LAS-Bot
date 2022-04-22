package com.las;

import com.las.annotation.BotRun;

@BotRun(qq = "1091569752", qqAuth = "123456", webpath = "target/classes/static", port = 8080)
public class App {

    public static void main(String[] args) {
        LASBot.run(App.class);
    }
}
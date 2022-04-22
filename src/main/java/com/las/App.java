package com.las;

import com.las.annotation.BotRun;

@BotRun(qq = "1091569752", qqAuth = "123456")
public class App {

    public static void main(String[] args) {
        LASBot.run(App.class);
    }
}
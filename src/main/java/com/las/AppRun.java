package com.las;

import com.las.annotation.BotRun;
import com.las.config.LasBot;

/**
 * @author dullwolf
 */
@BotRun(superQQ = "1091569752", botQQ = "2547170055", keyAuth = "dw0123456789")
public class AppRun {

    public static void main(String[] args) {
        LasBot.run(AppRun.class);
    }

}

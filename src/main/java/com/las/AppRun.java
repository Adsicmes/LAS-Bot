package com.las;

import com.las.annotation.BotRun;
import com.las.core.Bot;

/**
 * @author dullwolf
 */
@BotRun(superQQ = "1121492893", botQQ = "1091569752", keyAuth = "dw0123456789")
public class AppRun {

    public static void main(String[] args) {
        Bot.run(AppRun.class);
    }

}

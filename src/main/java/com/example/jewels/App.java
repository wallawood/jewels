package com.example.jewels;

import com.example.jewels.repository.Database;
import io.gemboot.GembootServer;

public class App {
    public static void main(String[] args) {
        Database.initialize();
        GembootServer.start(App.class);
    }
}

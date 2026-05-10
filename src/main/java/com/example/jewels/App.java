package com.example.jewels;

import com.example.jewels.repository.Database;
import io.github.wallawood.WallawoodServer;

public class App {
    public static void main(String[] args) {
        Database.initialize();
        WallawoodServer.start(App.class);
    }
}

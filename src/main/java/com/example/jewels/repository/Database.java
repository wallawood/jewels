package com.example.jewels.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String URL = "jdbc:sqlite:jewels.db";

    private Database() {}

    public static void initialize() {
        try (Connection c = connection(); Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    cert_hash     TEXT    UNIQUE,
                    display_name  TEXT    NOT NULL UNIQUE,
                    level         INTEGER NOT NULL DEFAULT 1,
                    password_hash TEXT
                )""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS jewels (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_id  INTEGER NOT NULL REFERENCES users(id),
                    body       TEXT    NOT NULL,
                    created_at TEXT    NOT NULL
                )""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS sessions (
                    token      TEXT    PRIMARY KEY,
                    user_id    INTEGER NOT NULL REFERENCES users(id),
                    expires_at TEXT    NOT NULL
                )""");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection connection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

package com.example.jewels.repository;

import com.example.jewels.repository.dto.Session;
import io.github.wallawood.annotations.Component;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class SessionRepository {

    public String create(long userId, Duration expiry) {
        String token = generateToken();
        String expiresAt = Instant.now().plus(expiry).toString();
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO sessions (token, user_id, expires_at) VALUES (?, ?, ?)")) {
            ps.setString(1, token);
            ps.setLong(2, userId);
            ps.setString(3, expiresAt);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public Session findByToken(String token) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM sessions WHERE token = ?")) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Session(
                        rs.getString("token"),
                        rs.getLong("user_id"),
                        Instant.parse(rs.getString("expires_at")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String token) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM sessions WHERE token = ?")) {
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByUserId(long userId) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM sessions WHERE user_id = ?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteExpired() {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM sessions WHERE expires_at < ?")) {
            ps.setString(1, Instant.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}

package com.example.jewels.repository;

import com.example.jewels.repository.dto.User;
import io.github.wallawood.annotations.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRepository {

    public User findByCertHash(String certHash) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE cert_hash = ?")) {
            ps.setString(1, certHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User create(String certHash, String displayName, int level) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (cert_hash, display_name, level) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, certHash);
            ps.setString(2, displayName);
            ps.setInt(3, level);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new User(keys.getLong(1), certHash, displayName, level);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(long id) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getLong("id"), rs.getString("cert_hash"),
                rs.getString("display_name"), rs.getInt("level"));
    }
}

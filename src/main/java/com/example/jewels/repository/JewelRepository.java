package com.example.jewels.repository;

import com.example.jewels.repository.dto.Jewel;
import io.gemboot.annotations.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class JewelRepository {

    private static final String SELECT = """
            SELECT j.*, u.display_name AS author_name FROM jewels j
            JOIN users u ON u.id = j.author_id""";

    public List<Jewel> findAll() {
        return query(SELECT + " ORDER BY j.id DESC");
    }

    public List<Jewel> search(String term) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE j.body LIKE ? ORDER BY j.id DESC")) {
            ps.setString(1, "%" + term + "%");
            return collect(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Jewel findById(long id) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE j.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(long authorId, String body) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO jewels (author_id, body, created_at) VALUES (?, ?, ?)")) {
            ps.setLong(1, authorId);
            ps.setString(2, body);
            ps.setString(3, Instant.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(long id, String body) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement("UPDATE jewels SET body = ? WHERE id = ?")) {
            ps.setString(1, body);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(long id) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM jewels WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByAuthor(long authorId) {
        try (Connection c = Database.connection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM jewels WHERE author_id = ?")) {
            ps.setLong(1, authorId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Jewel> query(String sql) {
        try (Connection c = Database.connection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            return collect(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Jewel> collect(ResultSet rs) throws SQLException {
        List<Jewel> list = new ArrayList<>();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    private Jewel map(ResultSet rs) throws SQLException {
        return new Jewel(rs.getLong("id"), rs.getLong("author_id"),
                rs.getString("author_name"), rs.getString("body"),
                Instant.parse(rs.getString("created_at")));
    }
}

package com.pahana.persistence.dao;

import com.pahana.persistence.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    /* ========== Auth ========== */

    // Simple boolean login check (matches your existing UserService.login)
    public boolean validateUser(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username=? AND password=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public User findByCredentials(String username, String password) {
        String sql = "SELECT id, username, password, role FROM users " +
                "WHERE username=? AND password=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /* ========== CRUD / Search ========== */

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password, role, created_at FROM users ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> search(String q) {
        List<User> list = new ArrayList<>();
        if (q == null) q = "";
        q = "%" + q.trim() + "%";
        String sql = "SELECT id, username, password, role, created_at " +
                "FROM users " +
                "WHERE CAST(id AS CHAR) LIKE ? OR username LIKE ? OR role LIKE ? " +
                "ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword()); // NOTE: plain text unless you add hashing everywhere
            ps.setString(3, u.getRole());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(User u) throws SQLException {
        // Password optional on update (only change if provided)
        boolean changePwd = u.getPassword() != null && !u.getPassword().trim().isEmpty();
        String sql = "UPDATE users SET username=?, role=?"
                + (changePwd ? ", password=?" : "")
                + " WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, u.getUsername());
            ps.setString(i++, u.getRole());
            if (changePwd) ps.setString(i++, u.getPassword());
            ps.setInt(i, u.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public User findById(int id) {
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ========== Mapping ========== */

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        // If your model has createdAt (LocalDateTime), uncomment next lines:
        // Timestamp ts = rs.getTimestamp("created_at");
        // if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}

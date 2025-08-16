package com.pahana.persistence.dao;

import com.pahana.persistence.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private final Connection conn;

    public UserDAOImpl(Connection conn) {
        this.conn = conn;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        // Match your model’s setter names; adjust if different.
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setRole(rs.getString("role"));
        // If your model has password/createdAt fields and you want them:
        // u.setPassword(rs.getString("password"));
        // u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    @Override
    public User findByCredentials(String username, String password) throws SQLException {
        // NOTE: plain-text for now. If your DB stores hashes, hash `password` before comparing.
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE username=? AND password=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public boolean validateUser(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username=? AND password=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, username, password, role, created_at FROM users ORDER BY id";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<User> search(String q) throws SQLException {
        if (q == null) q = "";
        q = "%" + q.trim() + "%";
        String sql = "SELECT id, username, password, role, created_at FROM users " +
                "WHERE username LIKE ? OR role LIKE ? ORDER BY username";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q);
            ps.setString(2, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean insert(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword()); // hash if needed
            ps.setString(3, u.getRole() == null ? "USER" : u.getRole());
            int n = ps.executeUpdate();
            if (n > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) u.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, role=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRole());
            ps.setInt(4, u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }
}

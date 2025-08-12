package com.pahana.persistence.dao;

import com.pahana.persistence.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username=? AND password=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // NEW: return full user (id, username, role). fullName stays null for now.
    public User findByCredentials(String username, String password) {
        String sql = "SELECT id, username, role FROM users WHERE username=? AND password=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(rs.getString("role"));
                    // u.setFullName(null); // table doesn't have it yet
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

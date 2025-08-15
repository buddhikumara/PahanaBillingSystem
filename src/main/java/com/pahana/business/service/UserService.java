package com.pahana.business.service;

import com.pahana.persistence.dao.UserDAO;
import com.pahana.persistence.model.User;
import com.pahana.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    // Preferred: pass a JDBC connection from your servlet init()
    public UserService(Connection conn) {
        this.userDAO = new UserDAO(conn);
    }
    public User loginAndGetUser(String username, String password) {
        return userDAO.findByCredentials(username, password);
    }
    // Convenience: if some code calls new UserService() with no args
    public UserService() {
        try {
            Connection conn = DBUtil.getConnection();
            this.userDAO = new UserDAO(conn);
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain DB connection", e);
        }
    }

    /* ===== Auth ===== */
    public boolean login(String username, String password) {
        return userDAO.validateUser(username, password);
    }

    /* ===== Management (Admin) ===== */
    public List<User> findAll() { return userDAO.findAll(); }

    public List<User> search(String q) { return userDAO.search(q); }

    public boolean existsByUsername(String username) { return userDAO.existsByUsername(username); }

    public boolean insert(User u) throws SQLException { return userDAO.insert(u); }

    public boolean update(User u) throws SQLException { return userDAO.update(u); }

    public boolean delete(int id) throws SQLException { return userDAO.delete(id); }

    public User findById(int id) { return userDAO.findById(id); }
}

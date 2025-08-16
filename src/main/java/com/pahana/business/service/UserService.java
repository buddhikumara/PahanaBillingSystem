package com.pahana.business.service;

import com.pahana.persistence.dao.UserDAOImpl;
import com.pahana.persistence.model.User;
import com.pahana.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {



    public User loginAndGetUser(String username, String password) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.findByCredentials(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public boolean login(String username, String password) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.validateUser(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public List<User> findAll() {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public List<User> search(String q) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.search(q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public boolean existsByUsername(String username) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.existsByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public boolean insert(User u) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.insert(u);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public boolean update(User u) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.update(u);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public boolean delete(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.delete(id);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    public User findById(int id) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            UserDAOImpl dao = new UserDAOImpl(conn);
            return dao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }
}

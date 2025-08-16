package com.pahana.persistence.dao;

import com.pahana.persistence.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    User findByCredentials(String username, String password) throws SQLException;
    boolean validateUser(String username, String password) throws SQLException;

    List<User> findAll() throws SQLException;
    List<User> search(String q) throws SQLException;

    boolean existsByUsername(String username) throws SQLException;

    boolean insert(User u) throws SQLException;
    boolean update(User u) throws SQLException;
    boolean delete(int id) throws SQLException;

    User findById(int id) throws SQLException;
}

package com.pahana.business.service;

import com.pahana.persistence.dao.UserDAO;
import com.pahana.persistence.model.User;
import java.sql.Connection;

public class UserService {
    private final UserDAO userDAO;

    public UserService(Connection conn) {
        this.userDAO = new UserDAO(conn);
    }

    public boolean login(String username, String password) {
        return userDAO.validateUser(username, password);
    }

    public User loginAndGetUser(String username, String password) {
        return userDAO.findByCredentials(username, password);
    }
}

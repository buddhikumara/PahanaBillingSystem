package com.pahana.persistence.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean validateUser(String username, String password) {
        try {
            System.out.println("DEBUG: Trying login -> " + username + " / " + password);

            // 🔹 Print all users first
            Statement debugStmt = conn.createStatement();
            ResultSet debugRs = debugStmt.executeQuery("SELECT username, password FROM users");
            System.out.println("=== USERS IN DB ===");
            while (debugRs.next()) {
                System.out.println("DB -> " + debugRs.getString("username") + " / " + debugRs.getString("password"));
            }
            System.out.println("===================");

            // 🔹 Validate
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            System.out.println("DEBUG: Login result -> " + exists);
            return exists;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}



package com.pahana.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/pahana_db"; // check your db
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);

                // 🔹 Debug: Show current database
//                Statement st = connection.createStatement();
//                ResultSet rs = st.executeQuery("SELECT DATABASE()");
//                if (rs.next()) {
//                    System.out.println("DEBUG: Connected to DB -> " + rs.getString(1));
//                }

//                // 🔹 Debug: Show all tables
//                rs = st.executeQuery("SHOW TABLES");
//                System.out.println("DEBUG: Tables in current DB:");
//                while (rs.next()) {
//                    System.out.println(" - " + rs.getString(1));
//                }

//                // 🔹 Auto-create default admin user if table is empty
//                rs = st.executeQuery("SELECT COUNT(*) FROM users");
//                rs.next();
//                if (rs.getInt(1) == 0) {
//                    st.executeUpdate(
//                            "INSERT INTO users (username, password, role) VALUES ('admin', '1234', 'ADMIN')"
//                    );
//                    System.out.println("DEBUG: Created default admin user -> admin / 1234");
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}

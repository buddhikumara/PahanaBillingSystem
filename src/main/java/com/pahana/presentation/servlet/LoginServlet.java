package com.pahana.presentation.servlet;

import com.pahana.business.service.UserService;
import com.pahana.util.DBUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DBUtil.getConnection();
            userService = new UserService(conn);


//            // 🔹 Print all existing users in DB for testing
//            System.out.println("=== Existing Users in Database ===");
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT username, password FROM users");
//            while (rs.next()) {
//                System.out.println("Username: " + rs.getString("username") +
//                        ", Password: " + rs.getString("password"));
//            }
//            System.out.println("=================================");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Database connection failed", e);
        }
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (userService.login(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            response.sendRedirect("dashboard.jsp");
        } else {
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}

package com.pahana.presentation.servlet;

import com.pahana.business.service.UserService;
import com.pahana.persistence.model.User;
import com.pahana.util.DBUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DBUtil.getConnection();
            userService = new UserService(conn);
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

        User user = userService.loginAndGetUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("authUser", user);
            session.setMaxInactiveInterval(60 * 30);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}

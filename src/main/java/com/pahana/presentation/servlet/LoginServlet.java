package com.pahana.presentation.servlet;

import com.pahana.business.service.UserService;
import com.pahana.persistence.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Stateless service (thread-safe to keep as a field)
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    // Show login page on GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // prevent caching of login page
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // If already logged in, go to dashboard
        HttpSession existing = request.getSession(false);
        if (existing != null && existing.getAttribute("authUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response); // JSP is outside WEB-INF
    }

    // Handle login on POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // ensure UTF-8 form read

        String username = trimOrEmpty(request.getParameter("username"));
        String password = trimOrEmpty(request.getParameter("password"));

        if (username.isEmpty() || password.isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userService.loginAndGetUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("authUser", user);
            session.setMaxInactiveInterval(60 * 30); // 30 mins
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}

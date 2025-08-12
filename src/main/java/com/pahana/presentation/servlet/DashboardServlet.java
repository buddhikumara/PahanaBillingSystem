package com.pahana.presentation.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/dashboard")

public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

    }
}

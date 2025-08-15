package com.pahana.presentation.servlet;

import com.pahana.business.service.UserService;
import com.pahana.persistence.model.User;
import com.pahana.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DBUtil.getConnection();
            userService = new UserService(conn);
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User auth = (User) req.getSession().getAttribute("authUser");
        if (auth == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        if (!"ADMIN".equals(auth.getRole())) { resp.sendError(403); return; }

        String searchFlag = req.getParameter("search");
        List<User> list = java.util.Collections.emptyList();
        boolean hasSearched = false;

        if (searchFlag != null) {
            hasSearched = true;
            String q = param(req, "q");
            list = (q.isEmpty()) ? userService.findAll() : userService.search(q);
            req.setAttribute("q", q);
        } else {
            req.setAttribute("q", "");
        }

        req.setAttribute("hasSearched", hasSearched);
        req.setAttribute("users", list);
        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User auth = (User) req.getSession().getAttribute("authUser");
        if (auth == null) { resp.sendError(401); return; }
        if (!"ADMIN".equals(auth.getRole())) { resp.sendError(403); return; }

        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                List<String> errs = validate(req, false);
                if (!errs.isEmpty()) {
                    req.setAttribute("formErrors", errs);
                    req.setAttribute("openModal", "add");
                    req.setAttribute("form", bind(req, false));
                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("users", userService.findAll());
                    req.getRequestDispatcher("/users.jsp").forward(req, resp);
                    return;
                }
                userService.insert(bind(req, false));
                req.getSession().setAttribute("flashSuccess", "User added successfully.");
                resp.sendRedirect(req.getContextPath() + "/users?search=1&q=");
                return;

            } else if ("update".equals(action)) {
                List<String> errs = validate(req, true);
                if (!errs.isEmpty()) {
                    req.setAttribute("formErrors", errs);
                    req.setAttribute("openModal", "edit");
                    req.setAttribute("form", bind(req, true));
                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("users", userService.findAll());
                    req.getRequestDispatcher("/users.jsp").forward(req, resp);
                    return;
                }
                userService.update(bind(req, true));
                req.getSession().setAttribute("flashSuccess", "User updated successfully.");
                resp.sendRedirect(req.getContextPath() + "/users?search=1&q=");
                return;

            } else if ("delete".equals(action)) {
                String id = param(req, "id");
                if (id.isEmpty()) req.getSession().setAttribute("flashError","Missing user id.");
                else {
                    try {
                        userService.delete(Integer.parseInt(id));
                        req.getSession().setAttribute("flashSuccess", "User deleted.");
                    } catch (NumberFormatException ex) {
                        req.getSession().setAttribute("flashError","Invalid user id.");
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/users?search=1&q=");
                return;
            }

            resp.sendError(400, "Unknown action");
        } catch (SQLException e) {
            req.getSession().setAttribute("flashError","Database error: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/users?search=1&q=");
        }
    }

    // ---------- helpers ----------
    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    private User bind(HttpServletRequest req, boolean forUpdate) {
        User u = new User();
        if (forUpdate) {
            String id = param(req, "id");
            if (!id.isEmpty()) try { u.setId(Integer.parseInt(id)); } catch (NumberFormatException ignored) {}
        }
        u.setUsername(param(req, "username"));
        String pwd = param(req, "password");
        u.setPassword(pwd); // NOTE: plain text unless you add hashing consistently
        u.setRole(param(req, "role"));
        return u;
    }

    private List<String> validate(HttpServletRequest req, boolean forUpdate) {
        List<String> errs = new ArrayList<>();
        String id = param(req, "id");
        String username = param(req, "username");
        String role = param(req, "role");
        String pwd = param(req, "password");
        String confirm = param(req, "confirm");

        if (forUpdate) {
            if (id.isEmpty()) errs.add("Missing user id.");
            else try { Integer.parseInt(id); } catch (NumberFormatException e) { errs.add("Invalid user id."); }
        }

        if (username.isEmpty()) errs.add("Username is required.");
        if (username.length() > 50) errs.add("Username must be ≤ 50 characters.");

        if (!( "ADMIN".equals(role) || "USER".equals(role) || "CASHIER".equals(role) )) {
            errs.add("Role must be ADMIN, USER, or CASHIER.");
        }

        if (!forUpdate) {
            if (pwd.isEmpty()) errs.add("Password is required.");
            else if (pwd.length() < 6) errs.add("Password must be at least 6 characters.");
            if (!pwd.equals(confirm)) errs.add("Password confirmation does not match.");
            if (errs.isEmpty() && userService.existsByUsername(username)) errs.add("Username already exists.");
        } else {
            if (!pwd.isEmpty()) {
                if (pwd.length() < 6) errs.add("Password must be at least 6 characters.");
                if (!pwd.equals(confirm)) errs.add("Password confirmation does not match.");
            }
        }
        return errs;
    }
}

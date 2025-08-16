package com.pahana.presentation.servlet;

import com.pahana.business.service.UserService;
import com.pahana.persistence.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private final UserService svc = new UserService(); // stateless service

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // Ensure authUser for navbar (mirror 'user' to 'authUser' if needed)
        HttpSession session = req.getSession();
        if (session.getAttribute("authUser") == null && session.getAttribute("user") != null) {
            session.setAttribute("authUser", session.getAttribute("user"));
        }

        // Move flash messages session -> request (so JSP toasts work)
        moveFlash(session, req);

        String q = trim(req.getParameter("q"));
        boolean hasSearched = "1".equals(req.getParameter("search")) || (q != null && !q.isEmpty());

        req.setAttribute("q", q == null ? "" : q);
        req.setAttribute("hasSearched", hasSearched);

        if (hasSearched) {
            List<User> users = (q == null || q.isEmpty()) ? svc.findAll() : svc.search(q);
            req.setAttribute("users", users);
        } else {
            req.setAttribute("users", Collections.emptyList());
        }

        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        HttpSession session = req.getSession();

        try {
            if ("add".equalsIgnoreCase(action)) {
                // form fields
                String username = trim(req.getParameter("username"));
                String role     = trim(req.getParameter("role"));
                String password = req.getParameter("password");
                String confirm  = req.getParameter("confirm");

                // validate
                List<String> errs = new ArrayList<>();
                if (isBlank(username)) errs.add("Username is required.");
                if (isBlank(role))     errs.add("Role is required.");
                if (isBlank(password)) errs.add("Password is required.");
                if (!isBlank(password) && password.length() < 6) errs.add("Password must be at least 6 characters.");
                if (!Objects.equals(password, confirm)) errs.add("Password and confirmation do not match.");
                if (!isBlank(username) && svc.existsByUsername(username)) errs.add("Username already exists.");

                if (!errs.isEmpty()) {
                    // reopen Add modal with errors and previously entered values
                    req.setAttribute("openModal", "add");
                    Map<String,Object> form = new HashMap<>();
                    form.put("username", username);
                    form.put("role", role == null ? "USER" : role);
                    req.setAttribute("form", form);
                    req.setAttribute("formErrors", errs);

                    // keep the screen state consistent
                    req.setAttribute("hasSearched", false);
                    req.setAttribute("q", "");
                    req.setAttribute("users", Collections.emptyList());
                    req.getRequestDispatcher("/users.jsp").forward(req, resp);
                    return;
                }

                User u = new User();
                u.setUsername(username);
                u.setRole(role);
                u.setPassword(password); // (hash later if needed)
                svc.insert(u);

                session.setAttribute("flashSuccess", "User created (ID: " + u.getId() + ").");
                resp.sendRedirect(req.getContextPath() + "/users?search=1");
                return;
            }

            if ("update".equalsIgnoreCase(action)) {
                Integer id = parseInt(req.getParameter("id"));
                String username = trim(req.getParameter("username"));
                String role     = trim(req.getParameter("role"));
                String password = req.getParameter("password");
                String confirm  = req.getParameter("confirm");

                List<String> errs = new ArrayList<>();
                if (id == null) errs.add("Missing user ID.");
                if (isBlank(username)) errs.add("Username is required.");
                if (!isBlank(password) && password.length() < 6) errs.add("New password must be at least 6 characters.");
                if (!Objects.equals(password, confirm)) errs.add("New password and confirmation do not match.");

                if (!errs.isEmpty()) {
                    req.setAttribute("openModal", "edit");
                    Map<String,Object> form = new HashMap<>();
                    form.put("id", id);
                    form.put("username", username);
                    form.put("role", role);
                    req.setAttribute("form", form);
                    req.setAttribute("formErrors", errs);

                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("users", svc.findAll());
                    req.getRequestDispatcher("/WEB-INF/users.jsp").forward(req, resp);
                    return;
                }

                // If password left blank -> keep existing (DAO handles NULL = keep)
                User u = new User();
                u.setId(id);
                u.setUsername(username);
                u.setRole(role);
                if (!isBlank(password)) u.setPassword(password); else u.setPassword(null);

                svc.update(u);
                session.setAttribute("flashSuccess", "User updated (ID: " + id + ").");
                resp.sendRedirect(req.getContextPath() + "/users?search=1");
                return;
            }

            if ("delete".equalsIgnoreCase(action)) {
                Integer id = parseInt(req.getParameter("id"));
                if (id == null) {
                    session.setAttribute("flashError", "Missing user ID.");
                } else {
                    svc.delete(id);
                    session.setAttribute("flashSuccess", "User deleted (ID: " + id + ").");
                }
                resp.sendRedirect(req.getContextPath() + "/users?search=1");
                return;
            }

            session.setAttribute("flashError", "Unknown action.");
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (SQLException e) {
            session.setAttribute("flashError", "DB error: " + safe(e.getMessage()));
            resp.sendRedirect(req.getContextPath() + "/users?search=1");
        }
    }

    /* ================= helpers ================= */

    private static void moveFlash(HttpSession session, HttpServletRequest req) {
        Object ok = session.getAttribute("flashSuccess");
        Object er = session.getAttribute("flashError");
        if (ok != null) { req.setAttribute("flashSuccess", ok); session.removeAttribute("flashSuccess"); }
        if (er != null) { req.setAttribute("flashError", er); session.removeAttribute("flashError"); }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static Integer parseInt(String s) { try { return Integer.valueOf(s); } catch (Exception e) { return null; } }
    private static String safe(String s) { return s == null ? "" : s.replace('\n',' ').replace('\r',' '); }
}

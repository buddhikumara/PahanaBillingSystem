package com.pahana.presentation.servlet;

import com.pahana.persistence.dao.CustomerDAO;
import com.pahana.persistence.dao.CustomerDAOImpl;   // <-- correct package
import com.pahana.persistence.model.Customer;
import com.pahana.persistence.model.User;
import com.pahana.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.regex.Pattern;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DBUtil.getConnection();
            customerDAO = new CustomerDAOImpl(conn);   // <-- no typo (DAOImpl)
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User u = (User) req.getSession().getAttribute("authUser");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        String searchFlag = req.getParameter("search"); // present only when user pressed Search
        List<Customer> list = Collections.emptyList();
        boolean hasSearched = false;

        if (searchFlag != null) {
            hasSearched = true;
            String q = req.getParameter("q");
            if (q == null || q.trim().isEmpty()) {
                list = customerDAO.findAll();        // empty query => ALL
            } else {
                list = customerDAO.search(q.trim()); // filtered search
            }
            req.setAttribute("q", (q == null ? "" : q));
        } else {
            // first load: show empty state, not the table
            req.setAttribute("q", "");
        }

        req.setAttribute("hasSearched", hasSearched);
        req.setAttribute("customers", list);
        req.getRequestDispatcher("/WEB-INF/customers.jsp").forward(req, resp);
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User u = (User) req.getSession().getAttribute("authUser");
        if (u == null) { resp.sendError(401); return; }

        String role   = u.getRole();
        String action = req.getParameter("action");  // add | update | delete

        try {
            if ("add".equals(action)) {
                if (!canAdd(role)) { resp.sendError(403); return; }

                List<String> errs = validate(req, false);
                if (!errs.isEmpty()) {
                    // show modal again with errors + prefilled values
                    req.setAttribute("formErrors", errs);
                    req.setAttribute("openModal", "add");
                    req.setAttribute("form", bind(req));
                    // show the table behind modal with ALL (nice UX)
                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("customers", customerDAO.findAll());
                    req.getRequestDispatcher("/WEB-INF/customers.jsp").forward(req, resp);
                    return;
                }
                customerDAO.insert(bind(req));
                req.getSession().setAttribute("flashSuccess", "Customer added successfully.");
                resp.sendRedirect(req.getContextPath() + "/customers?search=1&q=");
                return;

            } else if ("update".equals(action)) {
                if (!canEdit(role)) { resp.sendError(403); return; }

                List<String> errs = validate(req, true);
                if (!errs.isEmpty()) {
                    req.setAttribute("formErrors", errs);
                    req.setAttribute("openModal", "edit");
                    req.setAttribute("form", bind(req));
                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("customers", customerDAO.findAll());
                    req.getRequestDispatcher("/WEB-INF/customers.jsp").forward(req, resp);
                    return;
                }
                customerDAO.update(bind(req));
                req.getSession().setAttribute("flashSuccess", "Customer updated successfully.");
                resp.sendRedirect(req.getContextPath() + "/customers?search=1&q=");
                return;

            } else if ("delete".equals(action)) {
                if (!canDelete(role)) { resp.sendError(403); return; }
                String id = req.getParameter("customerId");
                if (id == null || id.trim().isEmpty()) {
                    req.getSession().setAttribute("flashError", "Missing customer id.");
                } else {
                    customerDAO.delete(id);
                    req.getSession().setAttribute("flashSuccess", "Customer deleted.");
                }
                resp.sendRedirect(req.getContextPath() + "/customers?search=1&q=");
                return;
            }

            resp.sendError(400, "Unknown action");
        } catch (SQLException e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Database error: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customers?search=1&q=");
        }
    }

    /** Server-side validation. forUpdate=true => skip duplicate-id check */
    private List<String> validate(HttpServletRequest req, boolean forUpdate) {
        List<String> errs = new ArrayList<>();

        String customerId = param(req, "customerId");
        String name       = param(req, "name");
        String phone      = param(req, "phone");
        String email      = param(req, "email");
        String address    = param(req, "address");
        String unitsStr   = param(req, "units");

        if (customerId.isEmpty()) errs.add("Customer ID is required.");
        if (customerId.length() > 20) errs.add("Customer ID must be ≤ 20 characters.");
        if (!customerId.matches("[A-Za-z0-9\\-]+")) errs.add("Customer ID can contain letters, numbers, and hyphens only.");

        if (name.isEmpty()) errs.add("Name is required.");

        if (!phone.isEmpty() && !Pattern.compile("^[0-9+\\- ]{7,15}$").matcher(phone).matches())
            errs.add("Phone must be 7–15 digits (you may use + - space).");

        if (!email.isEmpty() && !Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matcher(email).matches())
            errs.add("Please enter a valid email address.");

        if (!unitsStr.isEmpty()) {
            try {
                int u = Integer.parseInt(unitsStr);
                if (u < 0) errs.add("Units cannot be negative.");
            } catch (NumberFormatException e) {
                errs.add("Units must be a whole number.");
            }
        }

        // Unique id check for ADD
        if (!forUpdate && !customerId.isEmpty()) {
            if (customerDAO.existsById(customerId)) errs.add("Customer ID already exists.");
        }
        return errs;
    }

    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return (v == null) ? "" : v.trim();
    }

    private Customer bind(HttpServletRequest req) {
        Customer c = new Customer();
        c.setCustomerId(req.getParameter("customerId"));
        c.setName(req.getParameter("name"));
        c.setPhone(req.getParameter("phone"));
        c.setEmail(req.getParameter("email"));
        c.setAddress(req.getParameter("address"));
        String units = req.getParameter("units");
        if (units == null || units.trim().isEmpty()) {   // Java 8 compatible
            c.setUnits(null);
        } else {
            try { c.setUnits(Integer.parseInt(units)); } catch (NumberFormatException e) { c.setUnits(null); }
        }
        return c;
    }

    private static boolean canAdd(String role)    { return "ADMIN".equals(role) || "CASHIER".equals(role) || "USER".equals(role); }
    private static boolean canEdit(String role)   { return "ADMIN".equals(role); }
    private static boolean canDelete(String role) { return "ADMIN".equals(role); }
}

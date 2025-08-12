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

        String q = req.getParameter("q");
        List<Customer> list;

        if (q == null || q.trim().isEmpty()) {
            list = customerDAO.findAll();          // first load OR empty search → ALL
        } else {
            list = customerDAO.search(q.trim());   // filtered
        }

        req.setAttribute("customers", list);
        req.setAttribute("q", q == null ? "" : q); // echo in input
        req.getRequestDispatcher("/WEB-INF/customers.jsp").forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("authUser");
        if (u == null) { resp.sendError(401); return; }
        String role = u.getRole();
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                if (!canAdd(role)) { resp.sendError(403); return; }
                customerDAO.insert(bind(req));
            } else if ("update".equals(action)) {
                if (!canEdit(role)) { resp.sendError(403); return; }
                customerDAO.update(bind(req));
            } else if ("delete".equals(action)) {
                if (!canDelete(role)) { resp.sendError(403); return; }
                customerDAO.delete(req.getParameter("customerId")); // String PK
            } else {
                resp.sendError(400, "Unknown action");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/customers");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "DB error: " + e.getMessage());
            doGet(req, resp);
        }
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

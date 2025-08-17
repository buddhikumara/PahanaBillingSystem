package com.pahana.presentation.servlet;

import com.pahana.persistence.dao.ItemDAO;
import com.pahana.persistence.dao.ItemDAOImpl;
import com.pahana.persistence.model.Item;
import com.pahana.persistence.model.User;
import com.pahana.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@WebServlet("/items")
public class ItemServlet extends HttpServlet {
    private ItemDAO itemDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DBUtil.getConnection();
            itemDAO = new ItemDAOImpl(conn);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("authUser");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        if ("CASHIER".equals(u.getRole())) { resp.sendError(403); return; }

        String searchFlag = req.getParameter("search");
        List<Item> list = Collections.emptyList();
        boolean hasSearched = false;

        if (searchFlag != null) {
            hasSearched = true;
            String q = param(req, "q");
            list = (q.isEmpty()) ? itemDAO.findAll() : itemDAO.search(q);
            req.setAttribute("q", q);
        } else {
            req.setAttribute("q", "");
        }

        req.setAttribute("hasSearched", hasSearched);
        req.setAttribute("items", list);
        req.getRequestDispatcher("/WEB-INF/items.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("authUser");
        if (u == null) { resp.sendError(401); return; }
        if ("CASHIER".equals(u.getRole())) { resp.sendError(403); return; }

        String role = u.getRole();
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                if (!("ADMIN".equals(role) || "USER".equals(role))) { resp.sendError(403); return; }

                List<String> errs = validate(req, false);
                if (!errs.isEmpty()) {
                    req.setAttribute("formErrors", errs);
                    req.setAttribute("openModal", "add");
                    req.setAttribute("form", bind(req));
                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("items", itemDAO.findAll());
                    req.getRequestDispatcher("/WEB-INF/items.jsp").forward(req, resp);
                    return;
                }
                itemDAO.insert(bind(req));
                req.getSession().setAttribute("flashSuccess", "Item added successfully.");
                resp.sendRedirect(req.getContextPath() + "/items?search=1&q=");
                return;

            } else if ("update".equals(action)) {
                if (!"ADMIN".equals(role)) { resp.sendError(403); return; }

                List<String> errs = validate(req, true);
                if (!errs.isEmpty()) {
                    req.setAttribute("formErrors", errs);
                    req.setAttribute("openModal", "edit");
                    req.setAttribute("form", bind(req));
                    req.setAttribute("hasSearched", true);
                    req.setAttribute("q", "");
                    req.setAttribute("items", itemDAO.findAll());
                    req.getRequestDispatcher("/WEB-INF/items.jsp").forward(req, resp);
                    return;
                }
                itemDAO.update(bind(req));
                req.getSession().setAttribute("flashSuccess", "Item updated successfully.");
                resp.sendRedirect(req.getContextPath() + "/items?search=1&q=");
                return;

            } else if ("delete".equals(action)) {
                if (!"ADMIN".equals(role)) { resp.sendError(403); return; }
                String id = param(req, "itemId");
                if (id.isEmpty()) {
                    req.getSession().setAttribute("flashError", "Missing item id.");
                } else {
                    try {
                        itemDAO.delete(Integer.parseInt(id));
                        req.getSession().setAttribute("flashSuccess", "Item deleted.");
                    } catch (NumberFormatException nfe) {
                        req.getSession().setAttribute("flashError", "Invalid item id.");
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/items?search=1&q=");
                return;
            }

            resp.sendError(400, "Unknown action");
        } catch (SQLException e) {
            req.getSession().setAttribute("flashError", "Database error: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/items?search=1&q=");
        }
    }

    // ---------- helpers ----------
    private Item bind(HttpServletRequest req) {
        Item it = new Item();
        String id = param(req, "itemId");
        if (!id.isEmpty()) { try { it.setItemId(Integer.parseInt(id)); } catch (NumberFormatException ignored) {} }
        it.setItemName(param(req, "itemName"));
        it.setDescription(param(req, "description"));

        String cp = param(req, "costPrice");
        String rp = param(req, "retailPrice");
        try { it.setCostPrice(new BigDecimal(cp)); }   catch (Exception e) { it.setCostPrice(null); }
        try { it.setRetailPrice(new BigDecimal(rp)); } catch (Exception e) { it.setRetailPrice(null); }

        String q = param(req, "quantity");
        if (q.isEmpty()) it.setQuantity(null);
        else { try { it.setQuantity(Integer.parseInt(q)); } catch (Exception e) { it.setQuantity(null); } }
        return it;
    }

    private List<String> validate(HttpServletRequest req, boolean forUpdate) {
        List<String> errs = new ArrayList<>();
        String id   = param(req, "itemId");
        String name = param(req, "itemName");
        String desc = param(req, "description");
        String cp   = param(req, "costPrice");
        String rp   = param(req, "retailPrice");
        String qty  = param(req, "quantity");

        if (forUpdate) {
            if (id.isEmpty()) errs.add("Missing item ID.");
            else try { Integer.parseInt(id); } catch (NumberFormatException e) { errs.add("Invalid item ID."); }
        }

        if (name.isEmpty()) errs.add("Item name is required.");
        if (name.length() > 100) errs.add("Item name must be ≤ 100 characters.");
        if (desc.length() > 255) errs.add("Description must be ≤ 255 characters.");

        // --- prices: 1.00 to 10000.00, up to 2 decimals ---
        java.math.BigDecimal MIN = new java.math.BigDecimal("1.00");
        java.math.BigDecimal MAX = new java.math.BigDecimal("10000.00");

        java.math.BigDecimal bcp = null, brp = null;

        if (cp.isEmpty()) {
            errs.add("Cost price is required (1.00–10000.00, up to 2 decimals).");
        } else {
            try {
                bcp = new java.math.BigDecimal(cp);
                if (bcp.scale() > 2) errs.add("Cost price must have at most 2 decimals.");
                if (bcp.compareTo(MIN) < 0 || bcp.compareTo(MAX) > 0)
                    errs.add("Cost price must be between 1.00 and 10000.00.");
            } catch (NumberFormatException ex) {
                errs.add("Cost price must be a number (e.g., 199.99).");
            }
        }

        if (rp.isEmpty()) {
            errs.add("Retail price is required (1.00–10000.00, up to 2 decimals).");
        } else {
            try {
                brp = new java.math.BigDecimal(rp);
                if (brp.scale() > 2) errs.add("Retail price must have at most 2 decimals.");
                if (brp.compareTo(MIN) < 0 || brp.compareTo(MAX) > 0)
                    errs.add("Retail price must be between 1.00 and 10000.00.");
            } catch (NumberFormatException ex) {
                errs.add("Retail price must be a number (e.g., 299.99).");
            }
        }

        // Only compare if both parsed OK
        if (bcp != null && brp != null && brp.compareTo(bcp) < 0) {
            errs.add("Retail price cannot be lower than cost price.");
        }

        if (!qty.isEmpty()) {
            try {
                int q = Integer.parseInt(qty);
                if (q < 0) errs.add("Quantity cannot be negative.");
            } catch (NumberFormatException e) {
                errs.add("Quantity must be a whole number.");
            }
        }

        return errs;
    }


    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}

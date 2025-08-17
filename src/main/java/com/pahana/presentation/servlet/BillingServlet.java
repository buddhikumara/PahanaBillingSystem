package com.pahana.presentation.servlet;

import com.pahana.business.dto.BillDTO;
import com.pahana.business.dto.BillItemDTO;
import com.pahana.persistence.dao.BillingDAO;
import com.pahana.persistence.dao.CustomerDAOImpl;
import com.pahana.persistence.dao.ItemDAOImpl;
import com.pahana.persistence.model.Customer;
import com.pahana.persistence.model.Item;
import com.pahana.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

@WebServlet("/billing")
public class BillingServlet extends HttpServlet {

    private BillDTO cart(HttpServletRequest req){
        BillDTO bill = (BillDTO) req.getSession().getAttribute("billCart");
        if (bill == null) { bill = new BillDTO(); req.getSession().setAttribute("billCart", bill); }
        return bill;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            List<Customer> customers = new CustomerDAOImpl(c).findAll();
            req.setAttribute("customers", customers);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (c != null) try { c.close(); } catch (Exception ignore) {}
        }
        req.getRequestDispatcher("/WEB-INF/billing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String action = req.getParameter("action");
        BillDTO bill = cart(req);

        Connection c = null;
        try {
            c = DBUtil.getConnection();
            ItemDAOImpl itemDAO = new ItemDAOImpl(c);
            BillingDAO billingDAO = new BillingDAO(); // methods accept Connection

            switch (action) {
                case "start": {
                    bill.setCustomerId(req.getParameter("customerId"));
                    bill.setCustomerName(req.getParameter("customerName"));
                    bill.getItems().clear();
                    req.getSession().removeAttribute("stockWarning");
                    resp.sendRedirect(req.getContextPath()+"/billing");
                    return;
                }

                case "addItem": {
                    String codeOrName = req.getParameter("codeOrName");
                    int qty = Integer.parseInt(req.getParameter("qty"));

                    if (codeOrName != null) {
                        codeOrName = codeOrName.trim();
                        int dash = codeOrName.indexOf(" - ");
                        if (dash > 0) codeOrName = codeOrName.substring(0, dash).trim();
                    }

                    Item item = itemDAO.findByIdOrName(codeOrName);
                    if (item != null) {
                        BillItemDTO bi = new BillItemDTO();
                        bi.setItemId(item.getItemId());
                        bi.setItemName(item.getItemName());
                        bi.setUnitPrice(item.getRetailPrice().doubleValue());
                        bi.setQty(qty);
                        bi.setStockQty(item.getQuantity());
                        bill.getItems().add(bi);

                        if (item.getQuantity() <= 0) {
                            req.getSession().setAttribute("stockWarning", "No stock: " + item.getItemName() + ". Billing allowed.");
                        } else if (item.getQuantity() < qty) {
                            req.getSession().setAttribute("stockWarning", "Only " + item.getQuantity() + " in stock for " + item.getItemName() + ". Billing allowed.");
                        } else {
                            req.getSession().removeAttribute("stockWarning");
                        }
                    } else {
                        req.getSession().setAttribute("stockWarning", "Item not found for: " + codeOrName);
                    }
                    resp.sendRedirect(req.getContextPath()+"/billing");
                    return;
                }

                case "removeItem": {
                    int idx = Integer.parseInt(req.getParameter("index"));
                    if (idx >= 0 && idx < bill.getItems().size()) bill.getItems().remove(idx);
                    resp.sendRedirect(req.getContextPath()+"/billing");
                    return;
                }

                case "finish": {
                    bill.setPaymentType(req.getParameter("paymentType"));
                    try { bill.setPaidAmount(Double.parseDouble(req.getParameter("paidAmount"))); } catch (Exception ignored) {}
                    try { bill.setDiscount(Double.parseDouble(req.getParameter("discount"))); } catch (Exception ignored) {}

                    boolean prevAuto = c.getAutoCommit();
                    c.setAutoCommit(false);
                    try {
                        int billId = billingDAO.insertBill(c, bill);
                        billingDAO.insertBillItems(c, billId, bill.getItems());
                        billingDAO.reduceStock(c, bill.getItems());
                        c.commit();

                        req.getSession().removeAttribute("billCart");

                        String qs = String.format(
                                "?billId=%d&customerName=%s&paymentType=%s&paidAmount=%s&discount=%s",
                                billId,
                                java.net.URLEncoder.encode(String.valueOf(bill.getCustomerName()), "UTF-8"),
                                java.net.URLEncoder.encode(String.valueOf(bill.getPaymentType()), "UTF-8"),
                                java.net.URLEncoder.encode(String.valueOf(bill.getPaidAmount()), "UTF-8"),
                                java.net.URLEncoder.encode(String.valueOf(bill.getDiscount()), "UTF-8")
                        );
                        resp.sendRedirect(req.getContextPath()+"/invoice.pdf"+qs);
                    } catch (Exception ex) {
                        try { c.rollback(); } catch (Exception ignore) {}
                        throw ex;
                    } finally {
                        try { c.setAutoCommit(prevAuto); } catch (Exception ignore) {}
                    }
                    return;
                }
                default: {
                    resp.sendRedirect(req.getContextPath()+"/billing");
                    return;
                }
            }

        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
//            if (c != null) try { c.close(); } catch (Exception ignore) {}
        }
    }
}

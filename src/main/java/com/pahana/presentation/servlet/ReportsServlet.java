package com.pahana.presentation.servlet;

import com.pahana.persistence.dao.ReportDAO;
import com.pahana.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/reports")
public class ReportsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sFrom = req.getParameter("from");
        String sTo   = req.getParameter("to");

        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Colombo"));
        LocalDate from = (sFrom == null || sFrom.trim().isEmpty()) ? today : LocalDate.parse(sFrom.trim());
        LocalDate to   = (sTo   == null || sTo.trim().isEmpty())   ? today : LocalDate.parse(sTo.trim());

        String qItem = val(req.getParameter("qItem"));
        String qCust = val(req.getParameter("qCustomer"));
        String qBill = val(req.getParameter("qBill"));

        try (Connection c = DBUtil.getConnection()) {
            ReportDAO dao = new ReportDAO();

            Map<String,Object> summary = dao.summary(c, from, to);
            List<Map<String,Object>> daily = dao.dailySales(c, from, to);
            List<Map<String,Object>> itemwise = dao.itemWise(c, from, to, qItem, 100);
            boolean custSupported = dao.supportsCustomerJoin(c);
            List<Map<String,Object>> customerwise = custSupported ? dao.customerWise(c, from, to, qCust, 100) : Collections.emptyList();
            List<Map<String,Object>> bills = dao.bills(c, from, to, qBill, 200);

            req.setAttribute("from", from.toString());
            req.setAttribute("to", to.toString());

            req.setAttribute("summary", summary);
            req.setAttribute("daily", daily);
            req.setAttribute("itemwise", itemwise);
            req.setAttribute("customerwise", customerwise);
            req.setAttribute("bills", bills);
            req.setAttribute("customerJoin", custSupported);

            req.setAttribute("qItem", qItem);
            req.setAttribute("qCustomer", qCust);
            req.setAttribute("qBill", qBill);

            req.getRequestDispatcher("/WEB-INF/reports.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private static String val(String s){ return s==null? "": s.trim(); }
}

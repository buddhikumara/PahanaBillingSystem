// com.pahana.presentation.servlet.InvoicePDFServlet
package com.pahana.presentation.servlet;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pahana.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/invoice.pdf")
public class InvoicePDFServlet extends HttpServlet {

    static class Line { String name; int qty; double price; }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int billId = Integer.parseInt(req.getParameter("billId"));
        String customerName = req.getParameter("customerName");
        String paymentMethod = req.getParameter("paymentMethod");
        double paidAmount = parse(req.getParameter("paidAmount"));
        double discount = parse(req.getParameter("discount"));

        double totalAmount = 0, subTotal = 0;
        List<Line> lines = new ArrayList<>();

        try (Connection c = DBUtil.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT total_amount FROM bills WHERE bill_id=?")) {
                ps.setInt(1, billId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) totalAmount = rs.getDouble(1); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT it.item_name, bi.quantity, bi.unit_price " +
                            "FROM bill_items bi JOIN items it ON bi.item_id=it.item_id WHERE bi.bill_id=?")) {
                ps.setInt(1, billId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Line l = new Line();
                        l.name = rs.getString("item_name");
                        l.qty = rs.getInt("quantity");
                        l.price = rs.getDouble("unit_price");
                        lines.add(l);
                        subTotal += l.qty * l.price;
                    }
                }
            }
        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("Error: " + e.getMessage());
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=invoice-"+billId+".pdf");

        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();
            doc.add(new Paragraph("INVOICE #" + billId));
            if (customerName != null && !customerName.isEmpty()) doc.add(new Paragraph("Customer: " + customerName));
            doc.add(new Paragraph(" "));

            PdfPTable t = new PdfPTable(4);
            t.setWidths(new int[]{50,10,20,20});
            t.addCell("Item");
            t.addCell("Qty");
            t.addCell("Price");
            t.addCell("Total");
            for (Line l : lines) {
                t.addCell(l.name);
                t.addCell(String.valueOf(l.qty));
                t.addCell(String.format("%.2f", l.price));
                t.addCell(String.format("%.2f", l.qty * l.price));
            }
            doc.add(t);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Sub Total: " + String.format("%.2f", subTotal)));
            doc.add(new Paragraph("Discount : " + String.format("%.2f", discount)));
            doc.add(new Paragraph("Grand Total (DB): " + String.format("%.2f", totalAmount)));
            if (paymentMethod != null) {
                doc.add(new Paragraph("Payment: " + paymentMethod + " / Paid: " + String.format("%.2f", paidAmount)));
                doc.add(new Paragraph("Balance: " + String.format("%.2f", (paidAmount - totalAmount))));
            }
        } catch (Exception e) {
            resp.reset();
            resp.setContentType("text/plain");
            resp.getWriter().println("Error generating PDF: " + e.getMessage());
        } finally {
            doc.close();
        }
    }

    private double parse(String s){ try { return Double.parseDouble(s); } catch (Exception e){ return 0; } }
}

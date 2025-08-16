package com.pahana.presentation.servlet;

import com.pahana.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.*;

@WebServlet("/report/daily")
public class DailyReportPDFServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String dateParam = req.getParameter("date");
        ZoneId zone = ZoneId.of("Asia/Colombo");
        LocalDate day = (dateParam == null || dateParam.trim().isEmpty())
                ? LocalDate.now(zone)
                : LocalDate.parse(dateParam);

        ZonedDateTime zStart = day.atStartOfDay(zone);
        ZonedDateTime zEnd   = zStart.plusDays(1);
        Timestamp startTs = Timestamp.from(zStart.toInstant());
        Timestamp endTs   = Timestamp.from(zEnd.toInstant());

        int bills = 0;
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        String sql = "SELECT COUNT(*) AS cnt, COALESCE(SUM(total_amount),0) AS tot " +
                "FROM bills WHERE bill_date >= ? AND bill_date < ?";

        Connection c = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            c = DBUtil.getConnection();
            ps = c.prepareStatement(sql);
            ps.setTimestamp(1, startTs);
            ps.setTimestamp(2, endTs);
            rs = ps.executeQuery();
            if (rs.next()) {
                bills = rs.getInt("cnt");
                java.math.BigDecimal t = rs.getBigDecimal("tot");
                total = (t == null) ? java.math.BigDecimal.ZERO : t;
            }
        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("Error loading summary: " + e.getMessage());
            return;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            try { if (ps != null) ps.close(); } catch (Exception ignore) {}
            try { if (c != null) c.close(); } catch (Exception ignore) {}
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=daily-" + day + ".pdf");

        com.lowagie.text.Document doc = new com.lowagie.text.Document();
        try {
            com.lowagie.text.pdf.PdfWriter writer =
                    com.lowagie.text.pdf.PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();
            doc.add(new com.lowagie.text.Paragraph("Daily Cashier Summary"));
            doc.add(new com.lowagie.text.Paragraph("Date: " + day));
            doc.add(new com.lowagie.text.Paragraph(" "));
            com.lowagie.text.pdf.PdfPTable t = new com.lowagie.text.pdf.PdfPTable(2);
            t.setWidths(new int[]{40, 60});
            t.addCell("Bills");       t.addCell(String.valueOf(bills));
            t.addCell("Total Sales"); t.addCell(String.format("%.2f", total));
            doc.add(t);

            // Ask PDF viewer to open the print dialog
            writer.addJavaScript(
                    com.lowagie.text.pdf.PdfAction.javaScript(
                            "this.print({bUI:true,bSilent:false,bShrinkToFit:true});", writer));
        } catch (Exception e) {
            resp.reset();
            resp.setContentType("text/plain");
            resp.getWriter().println("Error generating PDF: " + e.getMessage());
        } finally {
            doc.close();
        }
    }
}

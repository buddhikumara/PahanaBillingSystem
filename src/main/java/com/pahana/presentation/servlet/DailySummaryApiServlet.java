package com.pahana.presentation.servlet;

import com.pahana.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.*;

@WebServlet("/api/summary")
public class DailySummaryApiServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String dateParam = req.getParameter("date");
        ZoneId zone = ZoneId.of("Asia/Colombo");
        LocalDate day = (dateParam == null || dateParam.trim().isEmpty())
                ? LocalDate.now(zone)
                : LocalDate.parse(dateParam); // expects YYYY-MM-DD

        ZonedDateTime zStart = day.atStartOfDay(zone);
        ZonedDateTime zEnd   = zStart.plusDays(1);
        Timestamp startTs = Timestamp.from(zStart.toInstant());
        Timestamp endTs   = Timestamp.from(zEnd.toInstant());

        String sql = "SELECT COUNT(*) AS bills, COALESCE(SUM(total_amount),0) AS total " +
                "FROM bills WHERE bill_date >= ? AND bill_date < ?";

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBUtil.getConnection();
            ps = c.prepareStatement(sql);
            ps.setTimestamp(1, startTs);
            ps.setTimestamp(2, endTs);
            rs = ps.executeQuery();

            int bills = 0;
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            if (rs.next()) {
                bills = rs.getInt("bills");
                java.math.BigDecimal t = rs.getBigDecimal("total");
                total = (t == null) ? java.math.BigDecimal.ZERO : t;
            }
            out.write("{\"bills\":" + bills + ",\"total\":" + total.toPlainString() + "}");
        } catch (Exception e) {
            resp.setStatus(500);
            out.write("{\"error\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            try { if (ps != null) ps.close(); } catch (Exception ignore) {}
            try { if (c != null) c.close(); } catch (Exception ignore) {}
        }
    }
}

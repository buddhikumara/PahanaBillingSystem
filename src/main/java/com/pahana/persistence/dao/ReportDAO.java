// src/main/java/com/pahana/persistence/dao/ReportDAO.java
package com.pahana.persistence.dao;

import java.sql.*;
import java.time.*;
import java.util.*;

public class ReportDAO {

    /* ===== Time helpers (Asia/Colombo) ===== */
    private static final ZoneId ZONE = ZoneId.of("Asia/Colombo");

    private static Timestamp dayStart(LocalDate d) {
        return Timestamp.from(d.atStartOfDay(ZONE).toInstant());
    }
    private static Timestamp dayEndExclusive(LocalDate d) {
        return Timestamp.from(d.plusDays(1).atStartOfDay(ZONE).toInstant());
    }

    private static Timestamp rangeStart(LocalDate from) { return dayStart(from); }
    private static Timestamp rangeEndExclusive(LocalDate to) { return dayEndExclusive(to); }

    /** Returns present column name (case-insensitive) or null. */
    private static String findColumn(Connection c, String table, String... candidates) throws SQLException {
        DatabaseMetaData md = c.getMetaData();
        try (ResultSet rs = md.getColumns(c.getCatalog(), null, table, null)) {
            Set<String> cols = new HashSet<>();
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                if (name != null) cols.add(name.toLowerCase(Locale.ROOT));
            }
            for (String cand : candidates) {
                if (cols.contains(cand.toLowerCase(Locale.ROOT))) return cand;
            }
            return null;
        }
    }

    /* ===== Summary (bills, gross/discount/net, items sold, cash/card, top item) ===== */
    public Map<String, Object> summary(Connection c, LocalDate from, LocalDate to) throws SQLException {
        Map<String, Object> out = new HashMap<>();

        // Optional columns
        String discCol = findColumn(c, "bills", "discount");
        String paidCol = findColumn(c, "bills", "paid_amount"); // not directly shown but detected if needed
        String custCol = findColumn(c, "bills", "customer_id", "customerId");

        // 1) Bills count + gross + discount + net
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) bills_count, COALESCE(SUM(total_amount),0) gross");
        if (discCol != null) sb.append(", COALESCE(SUM(").append(discCol).append("),0) discount_total");
        sb.append(" FROM bills WHERE bill_date >= ? AND bill_date < ?");

        try (PreparedStatement ps = c.prepareStatement(sb.toString())) {
            ps.setTimestamp(1, rangeStart(from));
            ps.setTimestamp(2, rangeEndExclusive(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("bills", rs.getLong("bills_count"));
                    double gross = rs.getBigDecimal("gross").doubleValue();
                    double disc = 0.0;
                    if (discCol != null) {
                        java.math.BigDecimal bd = rs.getBigDecimal("discount_total");
                        disc = (bd == null ? 0.0 : bd.doubleValue());
                    }
                    out.put("gross", gross);
                    out.put("discount", disc);
                    out.put("net", Math.max(0, gross - disc));
                }
            }
        }

        // 2) Items sold
        String sqlItems =
                "SELECT COALESCE(SUM(bi.quantity),0) items_sold " +
                        "FROM bill_items bi JOIN bills b ON b.bill_id = bi.bill_id " +
                        "WHERE b.bill_date >= ? AND b.bill_date < ?";
        try (PreparedStatement ps = c.prepareStatement(sqlItems)) {
            ps.setTimestamp(1, rangeStart(from));
            ps.setTimestamp(2, rangeEndExclusive(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) out.put("items_sold", rs.getLong("items_sold"));
            }
        }

        // 3) Payment split (we now store payment_type consistently)
        String paySplit =
                "SELECT " +
                        "  COALESCE(SUM(CASE WHEN payment_type='cash' THEN total_amount END),0) cash_total, " +
                        "  COALESCE(SUM(CASE WHEN payment_type='card' THEN total_amount END),0) card_total " +
                        "FROM bills WHERE bill_date >= ? AND bill_date < ?";
        try (PreparedStatement ps = c.prepareStatement(paySplit)) {
            ps.setTimestamp(1, rangeStart(from));
            ps.setTimestamp(2, rangeEndExclusive(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("cash", rs.getBigDecimal("cash_total").doubleValue());
                    out.put("card", rs.getBigDecimal("card_total").doubleValue());
                }
            }
        }

        // 4) Top item (by qty)
        String topSql =
                "SELECT i.item_id, i.item_name, SUM(bi.quantity) qty, COALESCE(SUM(bi.total_price),0) amount " +
                        "FROM bill_items bi " +
                        "JOIN items i ON i.item_id = bi.item_id " +
                        "JOIN bills b ON b.bill_id = bi.bill_id " +
                        "WHERE b.bill_date >= ? AND b.bill_date < ? " +
                        "GROUP BY i.item_id, i.item_name " +
                        "ORDER BY qty DESC LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(topSql)) {
            ps.setTimestamp(1, rangeStart(from));
            ps.setTimestamp(2, rangeEndExclusive(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> top = new HashMap<>();
                    top.put("item_id", rs.getInt("item_id"));
                    top.put("item_name", rs.getString("item_name"));
                    top.put("qty", rs.getLong("qty"));
                    top.put("amount", rs.getBigDecimal("amount").doubleValue());
                    out.put("top_item", top);
                }
            }
        }

        return out;
    }

    /* ===== Daily sales (rollup by date) ===== */
    public List<Map<String, Object>> dailySales(Connection c, LocalDate from, LocalDate to) throws SQLException {
        String sql =
                "SELECT DATE(bill_date) d, COUNT(*) bills, COALESCE(SUM(total_amount),0) total " +
                        "FROM bills WHERE bill_date >= ? AND bill_date < ? " +
                        "GROUP BY DATE(bill_date) ORDER BY d";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, rangeStart(from));
            ps.setTimestamp(2, rangeEndExclusive(to));
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("date", rs.getDate("d").toLocalDate().toString());
                    row.put("bills", rs.getLong("bills"));
                    row.put("total", rs.getBigDecimal("total").doubleValue());
                    list.add(row);
                }
                return list;
            }
        }
    }

    /* ===== Item-wise ===== */
    public List<Map<String, Object>> itemWise(Connection c, LocalDate from, LocalDate to, String q, int limit) throws SQLException {
        String base =
                "SELECT i.item_id, i.item_name, " +
                        "       COALESCE(SUM(bi.quantity),0) qty, COALESCE(SUM(bi.total_price),0) amount " +
                        "FROM bill_items bi " +
                        "JOIN items i ON i.item_id = bi.item_id " +
                        "JOIN bills b ON b.bill_id = bi.bill_id " +
                        "WHERE b.bill_date >= ? AND b.bill_date < ? ";
        boolean hasQ = q != null && !q.trim().isEmpty();
        if (hasQ) base += "AND (i.item_name LIKE ? OR CAST(i.item_id AS CHAR) LIKE ?) ";
        base += "GROUP BY i.item_id, i.item_name ORDER BY qty DESC ";
        if (limit > 0) base += "LIMIT " + limit;

        try (PreparedStatement ps = c.prepareStatement(base)) {
            int idx = 1;
            ps.setTimestamp(idx++, rangeStart(from));
            ps.setTimestamp(idx++, rangeEndExclusive(to));
            if (hasQ) {
                String like = "%" + q.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("item_id", rs.getInt("item_id"));
                    row.put("item_name", rs.getString("item_name"));
                    row.put("qty", rs.getLong("qty"));
                    row.put("amount", rs.getBigDecimal("amount").doubleValue());
                    list.add(row);
                }
                return list;
            }
        }
    }

    /* ===== Customer-wise (only if bills has a customer column) ===== */
    public boolean supportsCustomerJoin(Connection c) throws SQLException {
        return findColumn(c, "bills", "customer_id", "customerId") != null;
    }

    public List<Map<String, Object>> customerWise(Connection c, LocalDate from, LocalDate to, String q, int limit) throws SQLException {
        String custCol = findColumn(c, "bills", "customer_id", "customerId");
        if (custCol == null) return Collections.emptyList();

        String base =
                "SELECT b." + custCol + " AS cid, c.name, COUNT(*) bills, COALESCE(SUM(b.total_amount),0) amount " +
                        "FROM bills b LEFT JOIN customers c ON c.customerId = b." + custCol + " " +
                        "WHERE b.bill_date >= ? AND b.bill_date < ? ";
        boolean hasQ = q != null && !q.trim().isEmpty();
        if (hasQ) base += "AND (c.name LIKE ? OR b." + custCol + " LIKE ?) ";
        base += "GROUP BY b." + custCol + ", c.name ORDER BY amount DESC ";
        if (limit > 0) base += "LIMIT " + limit;

        try (PreparedStatement ps = c.prepareStatement(base)) {
            int i = 1;
            ps.setTimestamp(i++, rangeStart(from));
            ps.setTimestamp(i++, rangeEndExclusive(to));
            if (hasQ) {
                String like = "%" + q.trim() + "%";
                ps.setString(i++, like);
                ps.setString(i++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("customer_id", rs.getString("cid"));
                    row.put("name", rs.getString("name"));
                    row.put("bills", rs.getLong("bills"));
                    row.put("amount", rs.getBigDecimal("amount").doubleValue());
                    list.add(row);
                }
                return list;
            }
        }
    }

    /* ===== Bills list ===== */
    public List<Map<String, Object>> bills(Connection c, LocalDate from, LocalDate to, String q, int limit) throws SQLException {
        String custCol = findColumn(c, "bills", "customer_id", "customerId");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT b.bill_id, b.bill_date, b.total_amount");
        if (custCol != null) sb.append(", b.").append(custCol).append(" AS customer_id");
        sb.append(" FROM bills b WHERE b.bill_date >= ? AND b.bill_date < ? ");
        boolean hasQ = q != null && !q.trim().isEmpty();
        if (hasQ) {
            sb.append("AND (CAST(b.bill_id AS CHAR) LIKE ? ");
            if (custCol != null) sb.append("OR b.").append(custCol).append(" LIKE ? ");
            sb.append(") ");
        }
        sb.append("ORDER BY b.bill_date DESC ");
        if (limit > 0) sb.append("LIMIT ").append(limit);

        try (PreparedStatement ps = c.prepareStatement(sb.toString())) {
            int i = 1;
            ps.setTimestamp(i++, rangeStart(from));
            ps.setTimestamp(i++, rangeEndExclusive(to));
            if (hasQ) {
                String like = "%" + q.trim() + "%";
                ps.setString(i++, like);
                if (custCol != null) ps.setString(i++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bill_id", rs.getInt("bill_id"));
                    row.put("bill_date", rs.getTimestamp("bill_date").toLocalDateTime());
                    row.put("total_amount", rs.getBigDecimal("total_amount").doubleValue());
                    if (custCol != null) row.put("customer_id", rs.getString("customer_id"));
                    list.add(row);
                }
                return list;
            }
        }
    }
}

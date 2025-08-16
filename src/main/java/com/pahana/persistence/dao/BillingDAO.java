// src/main/java/com/pahana/persistence/dao/BillingDAO.java
package com.pahana.persistence.dao;

import com.pahana.business.dto.BillDTO;
import com.pahana.business.dto.BillItemDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class BillingDAO {

    /* -------- helpers -------- */
    private static boolean hasColumnIgnoreCase(Connection c, String table, String col) throws SQLException {
        DatabaseMetaData md = c.getMetaData();
        try (ResultSet rs = md.getColumns(null, null, table, null)) {
            while (rs.next()) {
                if (col.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) return true;
            }
        }
        return false;
    }

    /* -------- bill header -------- */
    public int insertBill(Connection c, BillDTO bill) throws SQLException {
        boolean hasCustomer = hasColumnIgnoreCase(c, "bills", "customer_id") || hasColumnIgnoreCase(c, "bills", "customerId");
        boolean hasPayment  = hasColumnIgnoreCase(c, "bills", "payment_method");
        boolean hasPaid     = hasColumnIgnoreCase(c, "bills", "paid_amount");
        boolean hasDiscount = hasColumnIgnoreCase(c, "bills", "discount");

        StringBuilder cols = new StringBuilder("total_amount,bill_date");
        StringBuilder vals = new StringBuilder("?,NOW()");

        if (hasCustomer) { cols.append(",customer_id");   vals.append(",?"); }
        if (hasPayment)  { cols.append(",payment_method");vals.append(",?"); }
        if (hasPaid)     { cols.append(",paid_amount");   vals.append(",?"); }
        if (hasDiscount) { cols.append(",discount");      vals.append(",?"); }

        String sql = "INSERT INTO bills (" + cols + ") VALUES (" + vals + ")";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setBigDecimal(i++, BigDecimal.valueOf(bill.getGrandTotal()));
            if (hasCustomer) ps.setString(i++, bill.getCustomerId());
            if (hasPayment)  ps.setString(i++, bill.getPaymentMethod());
            if (hasPaid)     ps.setBigDecimal(i++, BigDecimal.valueOf(bill.getPaidAmount()));
            if (hasDiscount) ps.setBigDecimal(i++, BigDecimal.valueOf(bill.getDiscount()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to insert bill");
    }

    /* -------- bill lines -------- */
    public void insertBillItems(Connection c, int billId, List<BillItemDTO> items) throws SQLException {
        String sql = "INSERT INTO bill_items (bill_id,item_id,quantity,unit_price,total_price) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (BillItemDTO it : items) {
                ps.setInt(1, billId);
                ps.setInt(2, it.getItemId());
                ps.setInt(3, it.getQty());
                ps.setBigDecimal(4, BigDecimal.valueOf(it.getUnitPrice()));
                ps.setBigDecimal(5, BigDecimal.valueOf(it.getTotal()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void reduceStock(Connection c, List<BillItemDTO> items) throws SQLException {
        String sql = "UPDATE items SET quantity = quantity - ? WHERE item_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (BillItemDTO it : items) {
                ps.setInt(1, it.getQty());
                ps.setInt(2, it.getItemId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}

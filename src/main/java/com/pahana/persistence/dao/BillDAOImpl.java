package com.pahana.persistence.dao.impl;

import com.pahana.persistence.dao.BillDAO;
import com.pahana.persistence.model.Bill;
import com.pahana.persistence.model.BillItem;
import com.pahana.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class BillDAOImpl implements BillDAO {

    @Override
    public boolean saveBill(Bill bill) {
        String insertBill = "INSERT INTO bills (total_amount) VALUES (?)";
        String insertItem = "INSERT INTO bill_items (bill_id, item_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            PreparedStatement psBill = con.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS);
            psBill.setDouble(1, bill.getTotalAmount());
            psBill.executeUpdate();

            ResultSet rs = psBill.getGeneratedKeys();
            int billId = 0;
            if (rs.next()) {
                billId = rs.getInt(1);
            }

            PreparedStatement psItem = con.prepareStatement(insertItem);
            for (BillItem item : bill.getItems()) {
                psItem.setInt(1, billId);
                psItem.setInt(2, item.getItemId());
                psItem.setInt(3, item.getQuantity());
                psItem.setDouble(4, item.getUnitPrice());
                psItem.setDouble(5, item.getTotalPrice());
                psItem.addBatch();
            }

            psItem.executeBatch();
            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

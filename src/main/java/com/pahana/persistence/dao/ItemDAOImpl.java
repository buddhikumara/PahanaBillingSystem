package com.pahana.persistence.dao;

import com.pahana.persistence.model.Item;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {
    private final Connection conn;
    public ItemDAOImpl(Connection conn){ this.conn = conn; }

    private Item map(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("item_id"),
                rs.getString("item_name"),
                rs.getString("description"),
                rs.getBigDecimal("cost_price"),
                rs.getBigDecimal("retail_price"),
                (Integer) rs.getObject("quantity")
        );
    }

    @Override public List<Item> findAll() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT item_id,item_name,description,cost_price,retail_price,quantity " +
                "FROM items ORDER BY item_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override public List<Item> search(String q) {
        List<Item> list = new ArrayList<>();
        if (q == null) q = "";
        q = "%" + q.trim() + "%";
        String sql = "SELECT item_id,item_name,description,cost_price,retail_price,quantity " +
                "FROM items " +
                "WHERE CAST(item_id AS CHAR) LIKE ? " +
                "   OR item_name LIKE ? " +
                "   OR description LIKE ? " +
                "ORDER BY item_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override public Item findById(int itemId) {
        String sql = "SELECT item_id,item_name,description,cost_price,retail_price,quantity " +
                "FROM items WHERE item_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override public boolean insert(Item i) throws SQLException {
        String sql = "INSERT INTO items (item_name,description,cost_price,retail_price,quantity) " +
                "VALUES (?,?,?,?,?)"; // no item_id: AUTO_INCREMENT
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, i.getItemName());
            ps.setString(2, i.getDescription());
            ps.setBigDecimal(3, i.getCostPrice());
            ps.setBigDecimal(4, i.getRetailPrice());
            if (i.getQuantity() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, i.getQuantity());
            return ps.executeUpdate() == 1;
        }
    }

    @Override public boolean update(Item i) throws SQLException {
        String sql = "UPDATE items SET item_name=?, description=?, cost_price=?, retail_price=?, quantity=? " +
                "WHERE item_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, i.getItemName());
            ps.setString(2, i.getDescription());
            ps.setBigDecimal(3, i.getCostPrice());
            ps.setBigDecimal(4, i.getRetailPrice());
            if (i.getQuantity() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, i.getQuantity());
            ps.setInt(6, i.getItemId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override public boolean delete(int itemId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM items WHERE item_id=?")) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() == 1;
        }
    }
}

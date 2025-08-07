package com.pahana.persistence.dao;

import com.pahana.persistence.model.Item;
import com.pahana.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {

    @Override
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (item_name, description, cost_price, retail_price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getCostPrice());
            ps.setDouble(4, item.getRetailPrice());
            ps.setInt(5, item.getQuantity());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items";
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Item(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("retail_price"),
                        rs.getInt("quantity")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }



    @Override
    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET item_name=?, description=?, cost_price=?, retail_price=?, quantity=? WHERE item_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getCostPrice());
            ps.setDouble(4, item.getRetailPrice());
            ps.setInt(5, item.getQuantity());
            ps.setInt(6, item.getItemId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteItem(int id) {
        String sql = "DELETE FROM items WHERE item_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

package com.pahana.persistence.dao;

import com.pahana.persistence.dao.CustomerDAO;
import com.pahana.persistence.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    private final Connection conn;
    public CustomerDAOImpl(Connection conn) { this.conn = conn; }
    @Override
    public List<Customer> search(String q) {
        List<Customer> list = new ArrayList<>();
        if (q == null) q = "";
        q = "%" + q.trim() + "%";
        String sql = "SELECT customerId,name,address,phone,email,units " +
                "FROM customers " +
                "WHERE customerId LIKE ? OR name LIKE ? OR phone LIKE ? OR email LIKE ? OR address LIKE ? " +
                "ORDER BY customerId DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) ps.setString(i, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Customer(
                            rs.getString("customerId"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            (Integer) rs.getObject("units")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customerId,name,address,phone,email,units FROM customers ORDER BY customerId DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer c = new Customer(
                        rs.getString("customerId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        (Integer) rs.getObject("units")
                );
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }



    @Override public Customer findById(String customerId) {
        String sql = "SELECT customerId,name,address,phone,email,units FROM customers WHERE customerId=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getString("customerId"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            (Integer) rs.getObject("units")
                    );
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override public boolean existsById(String customerId) {
        String sql = "SELECT 1 FROM customers WHERE customerId=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override public boolean insert(Customer c) throws SQLException {
        String sql = "INSERT INTO customers(customerId,name,address,phone,email,units) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            if (c.getUnits() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, c.getUnits());
            return ps.executeUpdate() == 1;
        }
    }

    @Override public boolean update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name=?, address=?, phone=?, email=?, units=? WHERE customerId=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            if (c.getUnits() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, c.getUnits());
            ps.setString(6, c.getCustomerId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override public boolean delete(String customerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE customerId=?")) {
            ps.setString(1, customerId);
            return ps.executeUpdate() == 1;
        }
    }


}

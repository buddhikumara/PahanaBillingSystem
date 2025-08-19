package com.pahana.business.service;

import com.pahana.persistence.dao.ItemDAO;
import com.pahana.persistence.dao.ItemDAOImpl;
import com.pahana.persistence.model.Item;
import com.pahana.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ItemService {
    private final ItemDAO dao;


    public ItemService(Connection conn) {
        this.dao = new ItemDAOImpl(conn);
    }

       public ItemService() {
        try {
            this.dao = new ItemDAOImpl(DBUtil.getConnection());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get DB connection", e);
        }
    }

    public List<Item> all()               { return dao.findAll(); }
    public List<Item> search(String q)    { return dao.search(q); }
    public Item getById(int id)           { return dao.findById(id); }
    public boolean add(Item i) throws SQLException    { return dao.insert(i); }
    public boolean update(Item i) throws SQLException { return dao.update(i); }
    public boolean delete(int id) throws SQLException { return dao.delete(id); }
}

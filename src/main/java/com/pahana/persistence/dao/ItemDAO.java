package com.pahana.persistence.dao;

import com.pahana.persistence.model.Item;
import java.sql.SQLException;
import java.util.List;

public interface ItemDAO {
    List<Item> findAll();
    List<Item> search(String q);
    Item findById(int itemId);
    boolean insert(Item i) throws SQLException;     // item_id auto
    boolean update(Item i) throws SQLException;     // needs item_id
    boolean delete(int itemId) throws SQLException;
}

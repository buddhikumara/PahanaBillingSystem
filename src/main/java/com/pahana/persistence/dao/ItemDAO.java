package com.pahana.persistence.dao;

import com.pahana.persistence.model.Item;
import java.sql.SQLException;
import java.util.List;

public interface ItemDAO {
    List<Item> findAll();
    List<Item> search(String q);

    Item findById(int itemId);
    Item findByIdOrName(String codeOrName);   // <--- add this
    boolean existsById(int itemId);

    boolean insert(Item i) throws SQLException;
    boolean update(Item i) throws SQLException;
    boolean delete(int itemId) throws SQLException;

    int getStock(int itemId);
    boolean reduceStock(int itemId, int qty) throws SQLException;
}

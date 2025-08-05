package com.pahana.persistence.dao;

import com.pahana.persistence.model.Item;
import java.util.List;

public interface ItemDAO {

    // Add a new item
    boolean addItem(Item item);

    // Get all items
    List<Item> getAllItems();

    // Update an existing item
    boolean updateItem(Item item);

    // Delete item by ID
    boolean deleteItem(int id);
}

package com.pahana.business.service;

import com.pahana.business.dto.ItemDTO;
import com.pahana.business.mapper.ItemMapper;
import com.pahana.persistence.dao.ItemDAOImpl;
import com.pahana.persistence.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemService {
    private final ItemDAOImpl itemDAO = new ItemDAOImpl();

    public boolean addItem(ItemDTO dto) {
        return itemDAO.addItem(ItemMapper.toEntity(dto));
    }

    public List<ItemDTO> getAllItems() {
        List<Item> entityList = itemDAO.getAllItems();
        return entityList.stream()
                .map(ItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean updateItem(ItemDTO dto) {
        return itemDAO.updateItem(ItemMapper.toEntity(dto));
    }

    public boolean deleteItem(int id) {
        return itemDAO.deleteItem(id);
    }
}

package com.pahana.business.mapper;

import com.pahana.business.dto.ItemDTO;
import com.pahana.persistence.model.Item;

import java.math.BigDecimal;

public class ItemMapper {

    public static ItemDTO toDTO(Item item) {
        if (item == null) return null;
        ItemDTO dto = new ItemDTO();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getDescription());
        dto.setCostPrice(item.getCostPrice());
        dto.setRetailPrice(item.getRetailPrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    public static Item toEntity(ItemDTO dto) {
        if (dto == null) return null;
        Item it = new Item();
        it.setItemId(dto.getItemId()); // may be null when adding
        it.setItemName(dto.getItemName());
        it.setDescription(dto.getDescription());
        // default prices to 0.00 if null
        it.setCostPrice(dto.getCostPrice() == null ? BigDecimal.ZERO : dto.getCostPrice());
        it.setRetailPrice(dto.getRetailPrice() == null ? BigDecimal.ZERO : dto.getRetailPrice());
        it.setQuantity(dto.getQuantity());
        return it;
    }
}

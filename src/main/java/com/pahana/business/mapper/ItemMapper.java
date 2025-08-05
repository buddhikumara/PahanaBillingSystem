package com.pahana.business.mapper;

import com.pahana.business.dto.ItemDTO;
import com.pahana.persistence.model.Item;

public class ItemMapper {

    public static ItemDTO toDTO(Item item) {
        return new ItemDTO(
                item.getItemId(),
                item.getItemName(),
                item.getDescription(),
                item.getCostPrice(),
                item.getRetailPrice(),
                item.getQuantity()
        );
    }

    public static Item toEntity(ItemDTO dto) {
        return new Item(
                dto.getItemId(),
                dto.getItemName(),
                dto.getDescription(),
                dto.getCostPrice(),
                dto.getRetailPrice(),
                dto.getQuantity()
        );
    }
}

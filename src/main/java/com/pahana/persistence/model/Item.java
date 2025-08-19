package com.pahana.persistence.model;

import java.math.BigDecimal;

public class Item {
    private Integer itemId;
    private String itemName;
    private String description;
    private BigDecimal costPrice;
    private BigDecimal retailPrice;
    private Integer quantity;

    public Item() {}

    public Item(Integer itemId, String itemName, String description,
                BigDecimal costPrice, BigDecimal retailPrice, Integer quantity) {
        this.itemId = itemId; this.itemName = itemName; this.description = description;
        this.costPrice = costPrice; this.retailPrice = retailPrice; this.quantity = quantity;
    }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getRetailPrice() { return retailPrice; }
    public void setRetailPrice(BigDecimal retailPrice) { this.retailPrice = retailPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public void setActive(int active) {
    }
}

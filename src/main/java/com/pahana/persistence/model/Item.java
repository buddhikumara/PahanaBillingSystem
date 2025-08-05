package com.pahana.persistence.model;

public class Item {
    private int itemId;
    private String itemName;
    private String description;
    private double costPrice;
    private double retailPrice;
    private int quantity;

    public Item() {}

    public Item(int itemId, String itemName, String description, double costPrice, double retailPrice, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.costPrice = costPrice;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public double getRetailPrice() { return retailPrice; }
    public void setRetailPrice(double retailPrice) { this.retailPrice = retailPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

package com.pahana.business.dto;

import java.math.BigDecimal;

public class ItemSalesDTO {
    private int itemId;
    private String itemName;
    private int qty;
    private BigDecimal amount;
    private int bills;

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public int getBills() { return bills; }
    public void setBills(int bills) { this.bills = bills; }
}

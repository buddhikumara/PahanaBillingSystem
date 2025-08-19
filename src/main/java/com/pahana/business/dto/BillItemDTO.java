// com.pahana.business.dto.BillItemDTO
package com.pahana.business.dto;
public class BillItemDTO {
    private Integer itemId;
    private String itemName;
    private int qty;
    private double unitPrice;
    private int stockQty;

    public double getTotal(){ return qty * unitPrice; }
    public boolean isOutOfStock(){ return stockQty <= 0; }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }
}

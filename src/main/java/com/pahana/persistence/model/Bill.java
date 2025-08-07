package com.pahana.persistence.model;

import java.time.LocalDateTime;
import java.util.List;

public class Bill {
    private int billId;
    private LocalDateTime billDate;
    private double totalAmount;
    private List<BillItem> items;

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

// Getters, Setters, Constructors
}

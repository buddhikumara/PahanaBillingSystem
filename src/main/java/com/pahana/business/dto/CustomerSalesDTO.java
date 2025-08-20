package com.pahana.business.dto;

import java.math.BigDecimal;

public class CustomerSalesDTO {
    private Integer customerId; // null = walk-in
    private String customerName;
    private int bills;
    private int qty;
    private BigDecimal amount;

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getBills() { return bills; }
    public void setBills(int bills) { this.bills = bills; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

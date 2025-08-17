// com.pahana.business.dto.BillDTO
package com.pahana.business.dto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDTO {
    private Integer billId;                 // DB-generated
    private String customerId;              // in-memory only (DB has no column)
    private String customerName;            // in-memory only
    private LocalDateTime createdAt = LocalDateTime.now();
    private final List<BillItemDTO> items = new ArrayList<>();
    // payment (in-memory only; not persisted because no columns)
    private String paymentMethod = "CASH";  // CASH/CARD (UI + PDF only)
    private double paidAmount;
    private double discount;
    private String paymentType;

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public double getSubTotal(){ return items.stream().mapToDouble(BillItemDTO::getTotal).sum(); }
    public double getGrandTotal(){ return Math.max(0, getSubTotal() - discount); }
    public double getBalance(){ return paidAmount - getGrandTotal(); }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BillItemDTO> getItems() {
        return items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}

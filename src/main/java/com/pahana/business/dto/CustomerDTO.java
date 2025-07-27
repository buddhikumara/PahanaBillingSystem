package com.pahana.business.dto;

public class CustomerDTO {
    private String accountNumber;
    private String name;
    private String address;
    private String phone;
    private int units;

    public CustomerDTO() {}

    public CustomerDTO(String accountNumber, String name, String address, String phone, int units) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.units = units;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    // You can use Lombok in the future to reduce this
}

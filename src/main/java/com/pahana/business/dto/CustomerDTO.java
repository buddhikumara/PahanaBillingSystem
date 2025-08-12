package com.pahana.business.dto;

public class CustomerDTO {
    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Integer units;

    public CustomerDTO() {}
    public CustomerDTO(String customerId, String name, String address, String phone, String email, Integer units) {
        this.customerId = customerId; this.name = name; this.address = address;
        this.phone = phone; this.email = email; this.units = units;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
}

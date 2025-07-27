package com.pahana.persistence.dao;

import com.pahana.persistence.model.Customer;
import java.util.List;

public interface CustomerDAO {
    void save(Customer customer);
    Customer findByAccountNumber(String accountNumber);
    List<Customer> findAll();
    void update(Customer customer);
    void delete(String accountNumber);
}

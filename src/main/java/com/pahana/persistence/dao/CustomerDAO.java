package com.pahana.persistence.dao;

import com.pahana.persistence.model.Customer;
import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    List<Customer> findAll();
    List<Customer> search(String q);

    Customer findById(String customerId);
    boolean existsById(String customerId);
    boolean insert(Customer c) throws SQLException;
    boolean update(Customer c) throws SQLException;
    boolean delete(String customerId) throws SQLException;


}

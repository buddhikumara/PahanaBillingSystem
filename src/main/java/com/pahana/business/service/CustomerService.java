package com.pahana.business.service;

import com.pahana.business.dto.CustomerDTO;
import com.pahana.business.mapper.CustomerMapper;
import com.pahana.persistence.dao.CustomerDAO;
import com.pahana.persistence.dao.CustomerDAOImpl;
import com.pahana.persistence.model.Customer;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    public void addCustomer(CustomerDTO dto) {
        Customer customer = CustomerMapper.toEntity(dto);
        customerDAO.save(customer);
    }

    public CustomerDTO getCustomer(String accountNumber) {
        Customer customer = customerDAO.findByAccountNumber(accountNumber);
        return customer != null ? CustomerMapper.toDTO(customer) : null;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customerList = customerDAO.findAll();
        return customerList.stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void updateCustomer(CustomerDTO dto) {
        Customer customer = CustomerMapper.toEntity(dto);
        customerDAO.update(customer);
    }

    public void deleteCustomer(String accountNumber) {
        customerDAO.delete(accountNumber);
    }
}

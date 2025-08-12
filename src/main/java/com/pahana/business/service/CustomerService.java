package com.pahana.business.service;

import com.pahana.business.dto.CustomerDTO;
import com.pahana.business.mapper.CustomerMapper;
// com.pahana.business.service.CustomerService
import com.pahana.persistence.dao.CustomerDAO;
import com.pahana.persistence.dao.CustomerDAOImpl;   // <-- not .dao.impl

import com.pahana.persistence.model.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    private final CustomerDAO dao;

    public CustomerService(Connection conn) {
        this.dao = new CustomerDAOImpl(conn); // correct impl & ctor
    }

    public List<CustomerDTO> getAll() {
        return dao.findAll().stream().map(CustomerMapper::toDTO).collect(Collectors.toList());
    }

    public CustomerDTO getById(String customerId) {
        return CustomerMapper.toDTO(dao.findById(customerId));
    }

    public boolean add(CustomerDTO dto) throws SQLException {
        Customer entity = CustomerMapper.toEntity(dto);
        return dao.insert(entity);
    }

    public boolean update(CustomerDTO dto) throws SQLException {
        Customer entity = CustomerMapper.toEntity(dto);
        return dao.update(entity);
    }

    public boolean delete(String customerId) throws SQLException {
        return dao.delete(customerId);
    }

    public boolean exists(String customerId) {
        return dao.existsById(customerId);
    }
}

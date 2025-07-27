package com.pahana.business.mapper;

import com.pahana.business.dto.CustomerDTO;
import com.pahana.persistence.model.Customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerDTO dto) {
        return new Customer(
                dto.getAccountNumber(),
                dto.getName(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getUnits()
        );
    }

    public static CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getAccountNumber(),
                customer.getName(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getUnitsConsumed()
        );
    }
}

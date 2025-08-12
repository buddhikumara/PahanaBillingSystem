package com.pahana.business.mapper;

import com.pahana.business.dto.CustomerDTO;
import com.pahana.persistence.model.Customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) return null;
        return new Customer(
                dto.getCustomerId(),
                dto.getName(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getUnits()
        );
    }

    public static CustomerDTO toDTO(Customer c) {
        if (c == null) return null;
        return new CustomerDTO(
                c.getCustomerId(),
                c.getName(),
                c.getAddress(),
                c.getPhone(),
                c.getEmail(),
                c.getUnits()
        );
    }
}

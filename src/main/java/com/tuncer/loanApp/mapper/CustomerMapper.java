package com.tuncer.loanApp.mapper;

import com.tuncer.loanApp.dto.CustomerDto;
import com.tuncer.loanApp.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDto customerToCustomerDto(Customer customer);

}

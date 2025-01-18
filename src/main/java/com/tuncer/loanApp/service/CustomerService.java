package com.tuncer.loanApp.service;

import com.tuncer.loanApp.dto.CustomerDto;
import com.tuncer.loanApp.mapper.CustomerMapper;
import com.tuncer.loanApp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDto> getCustomers() {
        return customerRepository.findAll().stream().map(CustomerMapper.INSTANCE::customerToCustomerDto).toList();
    }
}

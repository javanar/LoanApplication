package com.tuncer.loanApp.service;

import com.tuncer.loanApp.mapper.CustomerMapper;
import com.tuncer.loanApp.model.Customer;
import com.tuncer.loanApp.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp(){
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void shouldListCustomers() {
        //given
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("a");
        customer.setSurname("b");
        customer.setCreditLimit(10);

        List<Customer> customers = List.of(customer);

        //when
        when(customerRepository.findAll()).thenReturn(customers);

        //then
        assertThat(customerService.getCustomers().size()).isEqualTo(1);
    }

}

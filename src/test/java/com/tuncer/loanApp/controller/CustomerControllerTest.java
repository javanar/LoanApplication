package com.tuncer.loanApp.controller;

import com.tuncer.loanApp.dto.CustomerDto;
import com.tuncer.loanApp.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        customerController = new CustomerController(customerService);
    }

    @Test
    void shouldReturnCustomerList() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(1);
        customerDto.setCreditLimit(100);

        when(customerService.getCustomers()).thenReturn(List.of(customerDto));

        assertThat(customerController.getCustomers().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}

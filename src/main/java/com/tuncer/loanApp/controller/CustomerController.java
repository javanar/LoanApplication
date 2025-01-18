package com.tuncer.loanApp.controller;

import com.tuncer.loanApp.dto.CustomerDto;
import com.tuncer.loanApp.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customer/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDto>> getCustomers(){
        try {
            return ResponseEntity.ok(customerService.getCustomers());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

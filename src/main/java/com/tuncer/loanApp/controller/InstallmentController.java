package com.tuncer.loanApp.controller;

import com.tuncer.loanApp.dto.InstallmentDto;
import com.tuncer.loanApp.mapper.InstallmentMapper;
import com.tuncer.loanApp.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService installmentService;

    @GetMapping("/installment/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InstallmentDto>> listAllInstallments() {
        try {
            return ResponseEntity.ok(installmentService.getAllInstallments());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/installment/byLoan")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<InstallmentDto>> getInstallmentsByLoan(@RequestParam(required = true) int loanId){
        try {
            return ResponseEntity.ok(installmentService.getInstallmentsByLoanId(loanId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

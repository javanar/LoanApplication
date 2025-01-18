package com.tuncer.loanApp.controller;

import com.tuncer.loanApp.dto.LoanCreateDto;
import com.tuncer.loanApp.dto.LoanDto;
import com.tuncer.loanApp.dto.LoanPaymentDto;
import com.tuncer.loanApp.dto.LoanPaymentResultDto;
import com.tuncer.loanApp.dto.ResponseDto;
import com.tuncer.loanApp.exception.InsufficientCustomerCreditLimitException;
import com.tuncer.loanApp.exception.InvalidInterestRateException;
import com.tuncer.loanApp.exception.InvalidNumberOfInstallmentsException;
import com.tuncer.loanApp.exception.LoanNotFoundException;
import com.tuncer.loanApp.mapper.LoanMapper;
import com.tuncer.loanApp.model.Loan;
import com.tuncer.loanApp.request.CreateLoanRequest;
import com.tuncer.loanApp.request.PayLoanRequest;
import com.tuncer.loanApp.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/loan/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createLoan(@RequestBody CreateLoanRequest createLoanRequest){

        try {

            LoanCreateDto loanCreateDto = new LoanCreateDto();
            loanCreateDto.setCustomerId(createLoanRequest.getCustomerId());
            loanCreateDto.setAmount(createLoanRequest.getAmount());
            loanCreateDto.setInterestRate(createLoanRequest.getInterestRate());
            loanCreateDto.setNumberOfInstallments(createLoanRequest.getNumberOfInstallments());

            LoanDto loanDto = loanService.createLoan(loanCreateDto);
            loanDto.setMessage("Loan Created Successfully");
            return ResponseEntity.ok(loanDto);

        } catch (InsufficientCustomerCreditLimitException | InvalidNumberOfInstallmentsException | InvalidInterestRateException exception) {

            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage(exception.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/loan/find")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanDto>> findLoan(@RequestParam(required = false) Integer customerId,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) Integer numberOfInstallments ){
        try {
            return ResponseEntity.ok(loanService.findLoan(customerId, paid, numberOfInstallments));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("/loan/pay")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDto> payLoan(@RequestBody PayLoanRequest payLoanRequest) throws LoanNotFoundException {
        try {
            LoanPaymentDto loanPaymentDto = new LoanPaymentDto();
            loanPaymentDto.setLoanId(payLoanRequest.getLoanId());
            loanPaymentDto.setAmount(payLoanRequest.getAmount());
            return ResponseEntity.ok(loanService.payLoan(loanPaymentDto));
        } catch (LoanNotFoundException exception) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage(exception.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

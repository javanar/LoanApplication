package com.tuncer.loanApp.controller;

import com.tuncer.loanApp.dto.LoanDto;
import com.tuncer.loanApp.dto.LoanPaymentResultDto;
import com.tuncer.loanApp.exception.InsufficientCustomerCreditLimitException;
import com.tuncer.loanApp.exception.InvalidInterestRateException;
import com.tuncer.loanApp.exception.InvalidNumberOfInstallmentsException;
import com.tuncer.loanApp.exception.LoanNotFoundException;
import com.tuncer.loanApp.request.CreateLoanRequest;
import com.tuncer.loanApp.request.PayLoanRequest;
import com.tuncer.loanApp.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LoanControllerTest {

    @Mock
    private LoanService loanService;

    private LoanController loanController;

    @BeforeEach
    void setUp() {
        loanController = new LoanController(loanService);
    }

    @Test
    void shouldFindLoanGivenParams() {
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanId(1);

        List<LoanDto> loanDtos = List.of(loanDto);

        when(loanService.findLoan(any(), any(), any())).thenReturn(loanDtos);

        assertThat(loanController.findLoan(any(), any(), any()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldCreateLoanGivenRequest() throws InsufficientCustomerCreditLimitException, InvalidNumberOfInstallmentsException, InvalidInterestRateException {
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        createLoanRequest.setAmount(10000);
        createLoanRequest.setInterestRate(0.4f);
        createLoanRequest.setCustomerId(1);
        createLoanRequest.setNumberOfInstallments(12);

        LoanDto loanDto = new LoanDto();
        loanDto.setLoanId(1);

        when(loanService.createLoan(any())).thenReturn(loanDto);

        assertThat(loanController.createLoan(createLoanRequest).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldCatchExceptionWhenThrownWhenCreatingLoan() throws InsufficientCustomerCreditLimitException, InvalidNumberOfInstallmentsException, InvalidInterestRateException {
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        createLoanRequest.setAmount(10000);
        createLoanRequest.setInterestRate(0.4f);
        createLoanRequest.setCustomerId(1);
        createLoanRequest.setNumberOfInstallments(12);

        when(loanService.createLoan(any())).thenThrow(InvalidNumberOfInstallmentsException.class);

        assertThat(loanController.createLoan(createLoanRequest).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldPayLoanGivenRequest() throws LoanNotFoundException {
        PayLoanRequest payLoanRequest = new PayLoanRequest();
        payLoanRequest.setAmount(100);
        payLoanRequest.setLoanId(1);

        when(loanService.payLoan(any())).thenReturn(new LoanPaymentResultDto());

        assertThat(loanController.payLoan(payLoanRequest).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldCatchExceptionWhenThrownWhenPayingLoan() throws LoanNotFoundException {
        PayLoanRequest payLoanRequest = new PayLoanRequest();
        payLoanRequest.setAmount(100);
        payLoanRequest.setLoanId(1);

        when(loanService.payLoan(any())).thenThrow(LoanNotFoundException.class);

        assertThat(loanController.payLoan(payLoanRequest).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

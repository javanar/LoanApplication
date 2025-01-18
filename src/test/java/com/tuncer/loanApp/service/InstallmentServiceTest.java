package com.tuncer.loanApp.service;

import com.tuncer.loanApp.model.Installment;
import com.tuncer.loanApp.repository.InstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class InstallmentServiceTest {

    @Mock
    private InstallmentRepository installmentRepository;

    private InstallmentService installmentService;

    @BeforeEach
    void setUp(){
        installmentService = new InstallmentService(installmentRepository);
    }

    @Test
    void shouldListInstallments() {
        Installment installment = new Installment();
        List<Installment> installments = List.of(installment);

        when(installmentRepository.findAll()).thenReturn(installments);

        assertThat(installmentService.getAllInstallments().size()).isEqualTo(1);
    }

    @Test
    void shouldListInstallmentsByLoanId() {
        Installment installment = new Installment();
        List<Installment> installments = List.of(installment);

        when(installmentRepository.findAllByLoan_LoanId(anyInt())).thenReturn(installments);

        assertThat(installmentService.getInstallmentsByLoanId(5).size()).isEqualTo(1);
    }
}

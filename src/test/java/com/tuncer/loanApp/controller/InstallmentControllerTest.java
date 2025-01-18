package com.tuncer.loanApp.controller;

import com.tuncer.loanApp.dto.InstallmentDto;
import com.tuncer.loanApp.service.InstallmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class InstallmentControllerTest {

    @Mock
    private InstallmentService installmentService;

    private InstallmentController installmentController;

    @BeforeEach
    void setUp() {
        installmentController = new InstallmentController(installmentService);
    }

    @Test
    void shouldListInstallments() {
        InstallmentDto installmentDto = new InstallmentDto();
        List<InstallmentDto> installmentDtos = List.of(installmentDto);

        when(installmentService.getAllInstallments()).thenReturn(installmentDtos);

        assertThat(installmentController.listAllInstallments().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldListInstallmentsByLoanId() {
        InstallmentDto installmentDto = new InstallmentDto();
        List<InstallmentDto> installmentDtos = List.of(installmentDto);

        when(installmentService.getInstallmentsByLoanId(anyInt())).thenReturn(installmentDtos);

        assertThat(installmentController.getInstallmentsByLoan(anyInt()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

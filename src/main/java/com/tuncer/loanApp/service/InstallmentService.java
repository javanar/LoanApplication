package com.tuncer.loanApp.service;

import com.tuncer.loanApp.dto.InstallmentDto;
import com.tuncer.loanApp.mapper.InstallmentMapper;
import com.tuncer.loanApp.model.Installment;
import com.tuncer.loanApp.repository.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallmentService {

    private final InstallmentRepository installmentRepository;

    public List<InstallmentDto> getAllInstallments() {
        return installmentRepository.findAll().stream().
                map(InstallmentMapper.INSTANCE::installmentToInstallmentDto).toList();
    }

    public List<InstallmentDto> getInstallmentsByLoanId(int loanId) {
        return installmentRepository.findAllByLoan_LoanId(loanId).stream()
                .map(InstallmentMapper.INSTANCE::installmentToInstallmentDto).toList();
    }
}

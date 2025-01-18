package com.tuncer.loanApp.repository;

import com.tuncer.loanApp.model.Installment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Integer> {

    List<Installment> findAllByLoan_LoanId(int loanId);
    List<Installment> findAllByPaidAndLoan_LoanIdOrderByInstallmentNumber(boolean paid, int loanId, Pageable pageable);
    List<Installment> findAllByPaidAndLoan_LoanId(boolean paid, int loanId);
}

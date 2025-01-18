package com.tuncer.loanApp.repository;

import com.tuncer.loanApp.model.Customer;
import com.tuncer.loanApp.model.Installment;
import com.tuncer.loanApp.model.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InstallmentRepositoryTest {

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldFindALoanGivenValidLoanId() {
        //given
        Customer customer = new Customer();
        customer.setName("a");
        customer.setSurname("b");
        customer.setCreditLimit(10);
        customer = customerRepository.save(customer);

        Loan loan = new Loan();
        loan.setPaid(false);
        loan.setRequestedAmount(10);
        loan.setCustomer(customer);
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(0.2f);
        loan.setRequestedAmount(1000);
        loan.setRemainingAmount(1200);
        loan = loanRepository.save(loan);

        Installment installment = new Installment();
        installment.setLoan(loan);
        installment = installmentRepository.save(installment);

        //when
        List<Installment> installments = installmentRepository.findAllByLoan_LoanId(1);

        //then
        assertThat(installments.getFirst().getLoan()).isEqualTo(loan);
    }

    @Test
    void shouldFindAnUnpaidInstallmentGivenValidLoanId() {
        //given
        Customer customer = new Customer();
        customer.setName("a");
        customer.setSurname("b");
        customer.setCreditLimit(10);
        customer = customerRepository.save(customer);

        Loan loan = new Loan();
        loan.setPaid(true);
        loan.setRequestedAmount(10);
        loan.setCustomer(customer);
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(0.2f);
        loan.setRequestedAmount(1000);
        loan.setRemainingAmount(1200);
        loan = loanRepository.save(loan);

        Installment installment = new Installment();
        installment.setLoan(loan);
        installment = installmentRepository.save(installment);

        //when
        List<Installment> installments = installmentRepository.findAllByPaidAndLoan_LoanId(false, loan.getLoanId());

        //then
        assertThat(installments.getFirst().getLoan()).isEqualTo(loan);
        assertThat(installments.getFirst().getLoan().isPaid()).isTrue();
    }

    @Test
    void shouldFindAPaidInstallmentGivenValidLoanId() {
        //given
        Customer customer = new Customer();
        customer.setName("a");
        customer.setSurname("b");
        customer.setCreditLimit(10);
        customer = customerRepository.save(customer);

        Loan loan = new Loan();
        loan.setPaid(true);
        loan.setRequestedAmount(10);
        loan.setCustomer(customer);
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(0.2f);
        loan.setRequestedAmount(1000);
        loan.setRemainingAmount(1200);
        loan = loanRepository.save(loan);

        Installment installment = new Installment();
        installment.setLoan(loan);
        installment = installmentRepository.save(installment);

        //when
        List<Installment> installments = installmentRepository.findAllByPaidAndLoan_LoanIdOrderByInstallmentNumber(false, loan.getLoanId(), PageRequest.of(0,10));

        //then
        assertThat(installments.getFirst().getLoan()).isEqualTo(loan);
        assertThat(installments.getFirst().getLoan().isPaid()).isTrue();
    }
}

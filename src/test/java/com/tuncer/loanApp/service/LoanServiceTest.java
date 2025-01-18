package com.tuncer.loanApp.service;

import com.tuncer.loanApp.dto.LoanCreateDto;
import com.tuncer.loanApp.dto.LoanPaymentDto;
import com.tuncer.loanApp.exception.InsufficientCustomerCreditLimitException;
import com.tuncer.loanApp.exception.InvalidInterestRateException;
import com.tuncer.loanApp.exception.InvalidNumberOfInstallmentsException;
import com.tuncer.loanApp.exception.LoanNotFoundException;
import com.tuncer.loanApp.model.Config;
import com.tuncer.loanApp.model.Customer;
import com.tuncer.loanApp.model.Installment;
import com.tuncer.loanApp.model.Loan;
import com.tuncer.loanApp.repository.ConfigRepository;
import com.tuncer.loanApp.repository.CustomerRepository;
import com.tuncer.loanApp.repository.InstallmentRepository;
import com.tuncer.loanApp.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.tuncer.loanApp.querySpecification.LoanSpecification.byCustomer;
import static com.tuncer.loanApp.querySpecification.LoanSpecification.byNumberOfInstallments;
import static com.tuncer.loanApp.querySpecification.LoanSpecification.byPaid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ConfigRepository configRepository;
    @Mock
    private InstallmentRepository installmentRepository;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, customerRepository, configRepository, installmentRepository);
    }

    @Test
    void shouldFindLoanGivenArguments() {
        Integer customerId = 1;
        Boolean paid = false;
        Integer numberOfInstallments = 1;

        Loan loan = new Loan();
        List<Loan> loans = List.of(loan);

        when(loanRepository.findAll(any(Specification.class))).thenReturn(loans);

        assertThat(loanService.findLoan(customerId, paid, numberOfInstallments).size()).isEqualTo(1);
    }

    @Test
    void shouldCreateLoanGivenRequest() throws InsufficientCustomerCreditLimitException, InvalidNumberOfInstallmentsException, InvalidInterestRateException {
        LoanCreateDto loanCreateDto = new LoanCreateDto();
        loanCreateDto.setAmount(10000);
        loanCreateDto.setInterestRate(0.3f);
        loanCreateDto.setCustomerId(1);
        loanCreateDto.setNumberOfInstallments(12);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCreditLimit(100000000);

        Loan loan = new Loan();
        loan.setLoanId(1);

        Installment installment = new Installment();
        installment.setInstallmentId(1);

        String VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY = "VALID_NUMBER_OF_INSTALLMENTS";
        String MIN_INTEREST_RATE_CONFIGURATION_KEY = "MIN_INTEREST_RATE";
        String MAX_INTEREST_RATE_CONFIGURATION_KEY = "MAX_INTEREST_RATE";

        Config installmentNumber6 = new Config();
        installmentNumber6.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber6.setConfigurationValue("6");
        Config installmentNumber12 = new Config();
        installmentNumber12.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber12.setConfigurationValue("12");
        Config installmentNumber9 = new Config();
        installmentNumber9.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber9.setConfigurationValue("9");
        Config installmentNumber24 = new Config();
        installmentNumber24.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber24.setConfigurationValue("24");

        Config minRate = new Config();
        minRate.setConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY);
        minRate.setConfigurationValue("0.2");

        Config maxRate = new Config();
        maxRate.setConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY);
        maxRate.setConfigurationValue("0.5");

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        when(loanRepository.save(any())).thenReturn(loan);
        when(installmentRepository.save(any())).thenReturn(installment);
        when(configRepository.findByConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY))
                .thenReturn(List.of(installmentNumber6, installmentNumber9, installmentNumber12, installmentNumber24));
        when(configRepository.findByConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(minRate));
        when(configRepository.findByConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(maxRate));

        assertThat(loanService.createLoan(loanCreateDto).getLoanId()).isEqualTo(loan.getLoanId());
    }

    @Test
    void shouldThrowInsufficientLimitExceptionGivenInvalidRequest() {
        LoanCreateDto loanCreateDto = new LoanCreateDto();
        loanCreateDto.setAmount(10000);
        loanCreateDto.setInterestRate(0.3f);
        loanCreateDto.setCustomerId(1);
        loanCreateDto.setNumberOfInstallments(12);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCreditLimit(100);

        Loan loan = new Loan();
        loan.setLoanId(1);

        Installment installment = new Installment();
        installment.setInstallmentId(1);

        String VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY = "VALID_NUMBER_OF_INSTALLMENTS";
        String MIN_INTEREST_RATE_CONFIGURATION_KEY = "MIN_INTEREST_RATE";
        String MAX_INTEREST_RATE_CONFIGURATION_KEY = "MAX_INTEREST_RATE";

        Config installmentNumber6 = new Config();
        installmentNumber6.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber6.setConfigurationValue("6");
        Config installmentNumber12 = new Config();
        installmentNumber12.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber12.setConfigurationValue("12");
        Config installmentNumber9 = new Config();
        installmentNumber9.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber9.setConfigurationValue("9");
        Config installmentNumber24 = new Config();
        installmentNumber24.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber24.setConfigurationValue("24");

        Config minRate = new Config();
        minRate.setConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY);
        minRate.setConfigurationValue("0.2");

        Config maxRate = new Config();
        maxRate.setConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY);
        maxRate.setConfigurationValue("0.5");

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        when(loanRepository.save(any())).thenReturn(loan);
        when(installmentRepository.save(any())).thenReturn(installment);
        when(configRepository.findByConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY))
                .thenReturn(List.of(installmentNumber6, installmentNumber9, installmentNumber12, installmentNumber24));
        when(configRepository.findByConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(minRate));
        when(configRepository.findByConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(maxRate));

        assertThrows(InsufficientCustomerCreditLimitException.class, () -> loanService.createLoan(loanCreateDto));
    }

    @Test
    void shouldThrowInvalidInterestRateExceptionGivenInvalidRequest() {
        LoanCreateDto loanCreateDto = new LoanCreateDto();
        loanCreateDto.setAmount(10000);
        loanCreateDto.setInterestRate(0.9f);
        loanCreateDto.setCustomerId(1);
        loanCreateDto.setNumberOfInstallments(12);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCreditLimit(1000000000);

        Loan loan = new Loan();
        loan.setLoanId(1);

        Installment installment = new Installment();
        installment.setInstallmentId(1);

        String VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY = "VALID_NUMBER_OF_INSTALLMENTS";
        String MIN_INTEREST_RATE_CONFIGURATION_KEY = "MIN_INTEREST_RATE";
        String MAX_INTEREST_RATE_CONFIGURATION_KEY = "MAX_INTEREST_RATE";

        Config installmentNumber6 = new Config();
        installmentNumber6.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber6.setConfigurationValue("6");
        Config installmentNumber12 = new Config();
        installmentNumber12.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber12.setConfigurationValue("12");
        Config installmentNumber9 = new Config();
        installmentNumber9.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber9.setConfigurationValue("9");
        Config installmentNumber24 = new Config();
        installmentNumber24.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber24.setConfigurationValue("24");

        Config minRate = new Config();
        minRate.setConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY);
        minRate.setConfigurationValue("0.2");

        Config maxRate = new Config();
        maxRate.setConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY);
        maxRate.setConfigurationValue("0.5");

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        when(loanRepository.save(any())).thenReturn(loan);
        when(installmentRepository.save(any())).thenReturn(installment);
        when(configRepository.findByConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY))
                .thenReturn(List.of(installmentNumber6, installmentNumber9, installmentNumber12, installmentNumber24));
        when(configRepository.findByConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(minRate));
        when(configRepository.findByConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(maxRate));

        assertThrows(InvalidInterestRateException.class, () -> loanService.createLoan(loanCreateDto));
    }

    @Test
    void shouldThrowInvalidNumberOfInstallmentsExceptionGivenInvalidRequest() {
        LoanCreateDto loanCreateDto = new LoanCreateDto();
        loanCreateDto.setAmount(10000);
        loanCreateDto.setInterestRate(0.9f);
        loanCreateDto.setCustomerId(1);
        loanCreateDto.setNumberOfInstallments(5);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCreditLimit(1000000000);

        Loan loan = new Loan();
        loan.setLoanId(1);

        Installment installment = new Installment();
        installment.setInstallmentId(1);

        String VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY = "VALID_NUMBER_OF_INSTALLMENTS";
        String MIN_INTEREST_RATE_CONFIGURATION_KEY = "MIN_INTEREST_RATE";
        String MAX_INTEREST_RATE_CONFIGURATION_KEY = "MAX_INTEREST_RATE";

        Config installmentNumber6 = new Config();
        installmentNumber6.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber6.setConfigurationValue("6");
        Config installmentNumber12 = new Config();
        installmentNumber12.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber12.setConfigurationValue("12");
        Config installmentNumber9 = new Config();
        installmentNumber9.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber9.setConfigurationValue("9");
        Config installmentNumber24 = new Config();
        installmentNumber24.setConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);
        installmentNumber24.setConfigurationValue("24");

        Config minRate = new Config();
        minRate.setConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY);
        minRate.setConfigurationValue("0.2");

        Config maxRate = new Config();
        maxRate.setConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY);
        maxRate.setConfigurationValue("0.5");

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        when(loanRepository.save(any())).thenReturn(loan);
        when(installmentRepository.save(any())).thenReturn(installment);
        when(configRepository.findByConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY))
                .thenReturn(List.of(installmentNumber6, installmentNumber9, installmentNumber12, installmentNumber24));
        when(configRepository.findByConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(minRate));
        when(configRepository.findByConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY)).thenReturn(List.of(maxRate));

        assertThrows(InvalidNumberOfInstallmentsException.class, () -> loanService.createLoan(loanCreateDto));
    }

    @Test
    void shouldPayLoanGivenValidRequest() throws LoanNotFoundException {

        final String MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID_CONFIGURATION_KEY = "MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID";

        LoanPaymentDto loanPaymentDto = new LoanPaymentDto();
        loanPaymentDto.setAmount(15000);
        loanPaymentDto.setLoanId(1);

        Customer customer = new Customer();
        customer.setCreditLimit(1000000000);
        customer.setCustomerId(1);

        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setPaid(false);
        loan.setNumberOfInstallments(12);
        loan.setOriginalAmount(100000);
        loan.setCustomer(customer);

        Config config = new Config();
        config.setConfigurationKey(MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID_CONFIGURATION_KEY);
        config.setConfigurationValue("1");

        Customer customer2 = new Customer();
        customer2.setCreditLimit(2000000000);
        customer2.setCustomerId(2);

        Installment installment = new Installment();
        installment.setPaid(false);
        installment.setAmount(100);
        installment.setLoan(loan);
        installment.setDueDate(LocalDate.now());

        Installment paidInstallment = new Installment();
        paidInstallment.setPaid(true);
        paidInstallment.setAmount(100);
        paidInstallment.setLoan(loan);

        PageRequest pageRequest = PageRequest.of(0, 1);

        when(loanRepository.findById(anyInt())).thenReturn(Optional.of(loan));
        when(configRepository.findByConfigurationKey(MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID_CONFIGURATION_KEY))
                .thenReturn(List.of(config));
        when(installmentRepository.findAllByPaidAndLoan_LoanIdOrderByInstallmentNumber(false, 1, pageRequest))
                .thenReturn(List.of(installment));
        when(installmentRepository.save(any())).thenReturn(paidInstallment);
        when(customerRepository.save(any())).thenReturn(customer2);

        assertThat(loanService.payLoan(loanPaymentDto).getNumberOfInstallmentsPaid()).isEqualTo(1);

    }
}

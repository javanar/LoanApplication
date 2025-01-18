package com.tuncer.loanApp.service;

import com.tuncer.loanApp.dto.InstallmentPaymentResultDto;
import com.tuncer.loanApp.dto.LoanCreateDto;
import com.tuncer.loanApp.dto.LoanDto;
import com.tuncer.loanApp.dto.LoanPaymentDto;
import com.tuncer.loanApp.dto.LoanPaymentResultDto;
import com.tuncer.loanApp.exception.CustomerNotFoundException;
import com.tuncer.loanApp.exception.InsufficientCustomerCreditLimitException;
import com.tuncer.loanApp.exception.InvalidInterestRateException;
import com.tuncer.loanApp.exception.InvalidNumberOfInstallmentsException;
import com.tuncer.loanApp.exception.LoanNotFoundException;
import com.tuncer.loanApp.mapper.LoanMapper;
import com.tuncer.loanApp.model.Config;
import com.tuncer.loanApp.model.Customer;
import com.tuncer.loanApp.model.Installment;
import com.tuncer.loanApp.model.Loan;
import com.tuncer.loanApp.repository.ConfigRepository;
import com.tuncer.loanApp.repository.CustomerRepository;
import com.tuncer.loanApp.repository.InstallmentRepository;
import com.tuncer.loanApp.repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.tuncer.loanApp.querySpecification.LoanSpecification.byCustomer;
import static com.tuncer.loanApp.querySpecification.LoanSpecification.byNumberOfInstallments;
import static com.tuncer.loanApp.querySpecification.LoanSpecification.byPaid;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final ConfigRepository configRepository;
    private final InstallmentRepository installmentRepository;

    private static final String VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY = "VALID_NUMBER_OF_INSTALLMENTS";
    private static final String MIN_INTEREST_RATE_CONFIGURATION_KEY = "MIN_INTEREST_RATE";
    private static final String MAX_INTEREST_RATE_CONFIGURATION_KEY = "MAX_INTEREST_RATE";
    private static final String MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID_CONFIGURATION_KEY = "MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID";

    private static final double PRICE_PENALTY_FACTOR = 0.001;


    @Transactional
    public LoanDto createLoan(LoanCreateDto loanCreateDto) throws InsufficientCustomerCreditLimitException, InvalidNumberOfInstallmentsException, InvalidInterestRateException {

        Customer customer = customerRepository.findById(
                loanCreateDto.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found")
        );

        double loanTotalAmount = loanCreateDto.getAmount() * ( 1 + loanCreateDto.getInterestRate());

        checkCustomerLimit(loanTotalAmount, customer.getCreditLimit());
        checkNumberOfInstallments(loanCreateDto.getNumberOfInstallments());
        checkInterestRate(loanCreateDto.getInterestRate());

        customer.setCreditLimit(customer.getCreditLimit() - loanTotalAmount);
        Customer savedCustomer = customerRepository.save(customer);

        Loan loan = new Loan();
        loan.setOriginalAmount(loanTotalAmount);
        loan.setRequestedAmount(loanCreateDto.getAmount());
        loan.setCustomer(savedCustomer);
        loan.setInterestRate(loanCreateDto.getInterestRate());
        loan.setNumberOfInstallments(loanCreateDto.getNumberOfInstallments());
        loan.setPaid(false);

        Loan savedLoan = loanRepository.save(loan);

        createInstallments(savedLoan);

        return LoanMapper.INSTANCE.loanToLoanDto(savedLoan);
    }

    public List<LoanDto> findLoan(Integer customerId, Boolean paid, Integer numberOfInstallments) {
        Specification<Loan> filters = Specification.where(customerId == null ? null : byCustomer(customerId))
                .and(paid == null ? null : byPaid(paid))
                .and(numberOfInstallments == null ? null : byNumberOfInstallments(numberOfInstallments));

        return loanRepository.findAll(filters).stream().map(LoanMapper.INSTANCE::loanToLoanDto).toList();
    }

    @Transactional
    public LoanPaymentResultDto payLoan(LoanPaymentDto loanPaymentDto) throws LoanNotFoundException {
        int loanId = loanPaymentDto.getLoanId();
        double startingAmount = loanPaymentDto.getAmount();
        
        Loan loan = loanRepository.findById(loanId).orElseThrow(() ->
                new LoanNotFoundException("Unable to find the specified loan"));

        int maxInstallmentsCanBePaid = Integer.parseInt(configRepository.findByConfigurationKey(MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID_CONFIGURATION_KEY)
                .getFirst().getConfigurationValue());

        Pageable topInstallments = PageRequest.of(0, maxInstallmentsCanBePaid);
        List<Installment> installments =
                installmentRepository.findAllByPaidAndLoan_LoanIdOrderByInstallmentNumber(false, loanId, topInstallments);

        InstallmentPaymentResultDto installmentPaymentResultDto = payInstallments(startingAmount, installments);

        int remainingInstallmentsToBePaid = installmentRepository.findAllByPaidAndLoan_LoanId(false, loanId).size();

        if (installmentPaymentResultDto.getNumberOfPaidInstallments() > 0) {
            if (remainingInstallmentsToBePaid == 0) {
                loan.setPaid(true);
            }
            Customer customer = loan.getCustomer();
            customer.setCreditLimit(customer.getCreditLimit() + installmentPaymentResultDto.getPaidAmount());
            customerRepository.save(customer);
            loanRepository.save(loan);
        }

        return getLoanPaymentResultDto(installmentPaymentResultDto, startingAmount, remainingInstallmentsToBePaid);

    }

    private void checkCustomerLimit(double creditTotalAmount, double creditLimit) throws InsufficientCustomerCreditLimitException {
        if (creditTotalAmount > creditLimit) {
            throw new InsufficientCustomerCreditLimitException("Customer Credit Limit is not sufficient");
        }
    }

    private void checkNumberOfInstallments(int numberOfInstallments) throws InvalidNumberOfInstallmentsException {
        List<Config> validNumberOfInstallmentsConfigs =
                configRepository.findByConfigurationKey(VALID_NUMBER_OF_INSTALLMENTS_CONFIGURATION_KEY);

        boolean valid = validNumberOfInstallmentsConfigs.stream()
                .anyMatch(config -> Integer.parseInt(config.getConfigurationValue()) == numberOfInstallments);

        if (!valid) {
            throw new InvalidNumberOfInstallmentsException("Invalid number of installments");
        }
    }

    private void checkInterestRate(float interestRate) throws InvalidInterestRateException {
        float minimumInterestRate = Float.parseFloat(
                configRepository.findByConfigurationKey(MIN_INTEREST_RATE_CONFIGURATION_KEY).getFirst().getConfigurationValue()
        );

        float maximumInterestRate = Float.parseFloat(
                configRepository.findByConfigurationKey(MAX_INTEREST_RATE_CONFIGURATION_KEY).getFirst().getConfigurationValue()
        );

        if (interestRate > maximumInterestRate || interestRate < minimumInterestRate) {
            throw new InvalidInterestRateException("Invalid interest rate");
        }

    }

    private void createInstallments(Loan loan) {

        double installmentAmount = loan.getOriginalAmount() * (1 + loan.getInterestRate()) / loan.getNumberOfInstallments();

        for(int installmentNumber = 1; installmentNumber <= loan.getNumberOfInstallments(); installmentNumber++) {

            Installment installment = new Installment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setInstallmentNumber(installmentNumber);
            installment.setPaid(false);
            installment.setDueDate(LocalDate.now().plusMonths(installmentNumber).withDayOfMonth(1));
            installmentRepository.save(installment);
        }
    }

    private InstallmentPaymentResultDto payInstallments(double originalAmount, List<Installment> installments) {
        InstallmentPaymentResultDto installmentPaymentResultDto = new InstallmentPaymentResultDto();

        installmentPaymentResultDto.setPaidAmount(0);
        installmentPaymentResultDto.setNumberOfPaidInstallments(0);

        if (!installments.isEmpty()) {
            int paidInstallments = 0;
            double paidAmount = 0;

            for (Installment installment : installments) {
                double installmentAmount = calculateInstallmentAmount(installment.getAmount(), installment.getDueDate());

                if (originalAmount - paidAmount < installmentAmount) {
                    break;
                }

                installment.setPaid(true);
                installment.setPaymentDate(LocalDate.now());
                installment.setPaidAmount(installmentAmount);
                paidInstallments++;
                paidAmount += installmentAmount;
                installmentRepository.save(installment);
            }

            installmentPaymentResultDto.setNumberOfPaidInstallments(paidInstallments);
            installmentPaymentResultDto.setPaidAmount(paidAmount);

        }

        return installmentPaymentResultDto;
    }

    private LoanPaymentResultDto getLoanPaymentResultDto(InstallmentPaymentResultDto installmentPaymentResultDto,
                                                         double startingAmount, int remainingInstallmentsToBePaid) {
        LoanPaymentResultDto loanPaymentResultDto = new LoanPaymentResultDto();
        loanPaymentResultDto.setPaidAmount(installmentPaymentResultDto.getPaidAmount());
        loanPaymentResultDto.setNumberOfInstallmentsPaid(installmentPaymentResultDto.getNumberOfPaidInstallments());
        loanPaymentResultDto.setStartingAmount(startingAmount);
        loanPaymentResultDto.setRemainingAmount(startingAmount - installmentPaymentResultDto.getPaidAmount());
        loanPaymentResultDto.setRemainingNumberOfInstallmentsToBePaid(remainingInstallmentsToBePaid);
        return loanPaymentResultDto;
    }

    private double calculateInstallmentAmount(double amount, LocalDate dueDate) {
        LocalDate today = LocalDate.now();

        if (today.isEqual(dueDate)) {
            return amount;
        }

        long numberOfDays = ChronoUnit.DAYS.between(today, dueDate);

        if (today.isBefore(dueDate)) {
            return amount - (amount * PRICE_PENALTY_FACTOR * numberOfDays);
        } else {
            return amount + (amount * PRICE_PENALTY_FACTOR * numberOfDays);
        }

    }

}

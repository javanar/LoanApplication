package com.tuncer.loanApp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoanDto extends ResponseDto{

    private int loanId;
    private CustomerDto customer;
    private double originalAmount;
    private double remainingAmount;
    private double requestedAmount;
    private float interestRate;
    private int numberOfInstallments;
    private boolean paid;
}

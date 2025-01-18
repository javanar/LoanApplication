package com.tuncer.loanApp.dto;

import lombok.Data;

@Data
public class LoanCreateDto {

    private int customerId;
    private double amount;
    private float interestRate;
    private int numberOfInstallments;
}

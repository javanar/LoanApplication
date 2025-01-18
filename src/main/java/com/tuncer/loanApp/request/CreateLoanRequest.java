package com.tuncer.loanApp.request;

import lombok.Data;

@Data
public class CreateLoanRequest {

    private int customerId;
    private double amount;
    private float interestRate;
    private int numberOfInstallments;
}

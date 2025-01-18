package com.tuncer.loanApp.request;

import lombok.Data;

@Data
public class PayLoanRequest {

    private int loanId;
    private double amount;

}

package com.tuncer.loanApp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoanPaymentResultDto extends ResponseDto{

    private double startingAmount;
    private double remainingAmount;
    private double paidAmount;
    private int numberOfInstallmentsPaid;
    private int remainingNumberOfInstallmentsToBePaid;

}

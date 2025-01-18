package com.tuncer.loanApp.dto;

import lombok.Data;

@Data
public class InstallmentPaymentResultDto {

    private double paidAmount;
    private int numberOfPaidInstallments;
}

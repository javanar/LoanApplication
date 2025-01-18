package com.tuncer.loanApp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InstallmentDto {

    private int installmentId;
    private int installmentNumber;
    private int loanId;
    private double amount;
    private double paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private boolean paid;
}

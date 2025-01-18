package com.tuncer.loanApp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InstallmentDto {

    private int installmentId;
    private int installmentNumber;
    private int loanId;
    private double amount;
    private LocalDate dueDate;
    private boolean paid;
}

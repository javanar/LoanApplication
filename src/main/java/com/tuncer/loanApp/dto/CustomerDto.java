package com.tuncer.loanApp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerDto extends ResponseDto{

    private int customerId;
    private String name;
    private String surname;
    private double creditLimit;

}

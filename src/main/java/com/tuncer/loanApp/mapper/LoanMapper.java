package com.tuncer.loanApp.mapper;

import com.tuncer.loanApp.dto.LoanDto;
import com.tuncer.loanApp.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoanMapper {

    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    LoanDto loanToLoanDto(Loan loan);
}

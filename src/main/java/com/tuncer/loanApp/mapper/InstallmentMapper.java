package com.tuncer.loanApp.mapper;

import com.tuncer.loanApp.dto.InstallmentDto;
import com.tuncer.loanApp.model.Installment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InstallmentMapper {

    InstallmentMapper INSTANCE = Mappers.getMapper(InstallmentMapper.class);

    @Mapping(source = "loan.loanId", target = "loanId")
    @Mapping(source = "paidAmount", target = "paidAmount")
    InstallmentDto installmentToInstallmentDto(Installment installment);

}

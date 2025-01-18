package com.tuncer.loanApp.querySpecification;

import com.tuncer.loanApp.model.Loan;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

public class LoanSpecification {

    private LoanSpecification() {}

    public static Specification<Loan> byCustomer(Integer customerId){
        return ((root, query, builder) ->{
            Path<Loan> customer = root.get("customer");
            return builder.equal(customer.get("customerId"), customerId);
        });
    }

    public static Specification<Loan> byPaid(Boolean paid){
        return (root, query, builder) ->
            builder.equal(root.get("paid"), paid);
    }

    public static Specification<Loan> byNumberOfInstallments(Integer numberOfInstallments){
        return (root, query, builder) ->
            builder.equal(root.get("numberOfInstallments"), numberOfInstallments);
    }
}

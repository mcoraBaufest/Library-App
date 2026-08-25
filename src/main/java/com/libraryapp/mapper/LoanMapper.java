package com.libraryapp.mapper;

import com.libraryapp.dto.loan.response.LoanResponse;
import com.libraryapp.model.Loan;

public final class LoanMapper {

    // Impide crear instancias de una clase usada solo con métodos estáticos.
    private LoanMapper() {}

    // Convierte una entidad Loan en el DTO que expone la API.
    public static LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getUser().getUsername(),
                BookMapper.toResponse(loan.getBook()),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus());
    }
}

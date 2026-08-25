package com.libraryapp.mapper;

import com.libraryapp.dto.loan.response.LoanResponse;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.model.LoanStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanMapperTest {

    // Verifica la conversión de una entidad Loan a LoanResponse.
    @Test
    void shouldConvertLoanToResponse() {
        Book book = new Book("Clean Code", "Robert C. Martin", 2008);
        book.setId(1);
        LocalDateTime loanDate = LocalDateTime.of(2026, 8, 25, 10, 30);
        Loan loan = new Loan("juan", book, loanDate, LoanStatus.ACTIVE);
        loan.setId(7);

        LoanResponse response = LoanMapper.toResponse(loan);

        assertEquals(7, response.getId());
        assertEquals("juan", response.getUser());
        assertEquals(1, response.getBook().getId());
        assertEquals("Clean Code", response.getBook().getTitle());
        assertEquals(loanDate, response.getLoanDate());
        assertEquals(LoanStatus.ACTIVE, response.getStatus());
    }
}

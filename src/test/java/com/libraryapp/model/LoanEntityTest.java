package com.libraryapp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEntityTest {

    // Verifica que el constructor inicialice los datos del préstamo.
    @Test
    void shouldCreateLoanWithConstructorValues() {
        Book book = new Book("Clean Code", "Robert C. Martin", 2008);
        LocalDateTime loanDate = LocalDateTime.of(2026, 8, 25, 10, 30);

        Loan loan = new Loan("juan", book, loanDate, LoanStatus.ACTIVE);

        assertEquals("juan", loan.getUser());
        assertEquals(book, loan.getBook());
        assertEquals(loanDate, loan.getLoanDate());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
    }

    // Verifica que los setters actualicen los datos de la entidad.
    @Test
    void shouldUpdateLoanWithSetters() {
        Loan loan = new Loan();
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 26, 12, 0);

        loan.setId(1);
        loan.setUser("maria");
        loan.setReturnDate(returnDate);
        loan.setStatus(LoanStatus.RETURNED);

        assertEquals(1, loan.getId());
        assertEquals("maria", loan.getUser());
        assertEquals(returnDate, loan.getReturnDate());
        assertEquals(LoanStatus.RETURNED, loan.getStatus());
    }
}

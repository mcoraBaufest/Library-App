package com.libraryapp.model;

import org.junit.jupiter.api.Test;

import com.libraryapp.enums.LoanStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEntityTest {

    // Verifica que el constructor inicialice los datos del préstamo.
    @Test
    void shouldCreateLoanWithConstructorValues() {
        Book book = new Book("Clean Code", "Robert C. Martin", 2008);
        LocalDateTime loanDate = LocalDateTime.of(2026, 8, 25, 10, 30);

        User user = new User("juan", "juan@example.com");
        Loan loan = new Loan(user, book, loanDate, LoanStatus.ACTIVE);

        assertEquals(user, loan.getUser());
        assertEquals(book, loan.getBook());
        assertEquals(loanDate, loan.getLoanDate());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
    }

    // Verifica que los setters actualicen los datos de la entidad.
    @Test
    void shouldUpdateLoanWithSetters() {
        Loan loan = new Loan();
        User user = new User("maria", "maria@example.com");
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 26, 12, 0);

        loan.setId(1);
        loan.setUser(user);
        loan.setReturnDate(returnDate);
        loan.setStatus(LoanStatus.RETURNED);

        assertEquals(1, loan.getId());
        assertEquals(user, loan.getUser());
        assertEquals(returnDate, loan.getReturnDate());
        assertEquals(LoanStatus.RETURNED, loan.getStatus());
    }
}

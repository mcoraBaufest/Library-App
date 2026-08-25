package com.libraryapp.dto.loan.response;

import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.enums.LoanStatus;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanResponseTest {

    // Verifica que el constructor copie todos los valores del response.
    @Test
    void shouldCreateResponseWithConstructorValues() {
        BookResponse book = new BookResponse(1, "Clean Code", "Robert C. Martin", 2008);
        LocalDateTime loanDate = LocalDateTime.of(2026, 8, 25, 10, 30);

        LoanResponse response = new LoanResponse(
                7, "juan", book, loanDate, null, LoanStatus.ACTIVE);

        assertEquals(7, response.getId());
        assertEquals("juan", response.getUser());
        assertEquals(book, response.getBook());
        assertEquals(loanDate, response.getLoanDate());
        assertEquals(LoanStatus.ACTIVE, response.getStatus());
    }

    // Verifica que los setters y getters almacenen los valores correctamente.
    @Test
    void shouldStoreValuesWithSetters() {
        LoanResponse response = new LoanResponse();
        BookResponse book = new BookResponse(2, "Refactoring", "Martin Fowler", 2018);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 26, 12, 0);

        response.setId(8);
        response.setUser("maria");
        response.setBook(book);
        response.setLoanDate(returnDate.minusDays(1));
        response.setReturnDate(returnDate);
        response.setStatus(LoanStatus.RETURNED);

        assertEquals(8, response.getId());
        assertEquals("maria", response.getUser());
        assertEquals(book, response.getBook());
        assertEquals(returnDate.minusDays(1), response.getLoanDate());
        assertEquals(returnDate, response.getReturnDate());
        assertEquals(LoanStatus.RETURNED, response.getStatus());
    }
}

package com.libraryapp.dto.loan.response;

import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.model.LoanStatus;

import java.time.LocalDateTime;

public class LoanResponse {

    private Integer id;
    private String user;
    private BookResponse book;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private LoanStatus status;

    // Constructor vacío usado por Jackson y frameworks.
    public LoanResponse() {}

    // Crea la respuesta completa de un préstamo.
    public LoanResponse(Integer id, String user, BookResponse book, LocalDateTime loanDate,
                        LocalDateTime returnDate, LoanStatus status) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Devuelve el identificador del préstamo.
    public Integer getId() {
        return id;
    }

    // Asigna el identificador del préstamo.
    public void setId(Integer id) {
        this.id = id;
    }

    // Devuelve el usuario del préstamo.
    public String getUser() {
        return user;
    }

    // Asigna el usuario del préstamo.
    public void setUser(String user) {
        this.user = user;
    }

    // Devuelve el libro incluido en la respuesta.
    public BookResponse getBook() {
        return book;
    }

    // Asigna el libro incluido en la respuesta.
    public void setBook(BookResponse book) {
        this.book = book;
    }

    // Devuelve la fecha de inicio del préstamo.
    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    // Asigna la fecha de inicio del préstamo.
    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    // Devuelve la fecha de devolución.
    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    // Asigna la fecha de devolución.
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    // Devuelve el estado del préstamo.
    public LoanStatus getStatus() {
        return status;
    }

    // Asigna el estado del préstamo.
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}

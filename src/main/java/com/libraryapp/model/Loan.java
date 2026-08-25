package com.libraryapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", referencedColumnName = "username", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "loan_date", nullable = false)
    private LocalDateTime loanDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    // Constructor vacío requerido por JPA.
    public Loan() {}

    // Crea un préstamo con usuario, libro, fecha y estado inicial.
    public Loan(User user, Book book, LocalDateTime loanDate, LoanStatus status) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
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

    // Devuelve el usuario asociado al préstamo.
    public User getUser() {
        return user;
    }

    // Asigna el usuario asociado al préstamo.
    public void setUser(User user) {
        this.user = user;
    }

    // Devuelve el libro prestado.
    public Book getBook() {
        return book;
    }

    // Asigna el libro prestado.
    public void setBook(Book book) {
        this.book = book;
    }

    // Devuelve la fecha en que se creó el préstamo.
    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    // Asigna la fecha de creación del préstamo.
    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    // Devuelve la fecha de devolución, si ya fue devuelto.
    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    // Asigna la fecha de devolución del préstamo.
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    // Devuelve el estado actual del préstamo.
    public LoanStatus getStatus() {
        return status;
    }

    // Actualiza el estado del préstamo.
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}

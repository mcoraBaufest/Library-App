package com.libraryapp.service;

import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.dto.loan.response.LoanResponse;
import com.libraryapp.mapper.BookMapper;
import com.libraryapp.mapper.LoanMapper;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.model.LoanStatus;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final PriorityQueue<Book> loanQueue;

    // Inicializa las dependencias y la cola ordenada por año descendente.
    public LoanService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.loanQueue = new PriorityQueue<>(Comparator.comparing(Book::getYear).reversed());
    }

    // Agrega a la cola un libro existente que todavía no estaba en ella.
    public Optional<Boolean> addToLoanQueue(Integer bookId) {
        return bookRepository.findById(bookId).map(book -> {
            if (loanQueue.contains(book)) return false;
            loanQueue.offer(book);
            return true;
        });
    }

    // Consulta el libro prioritario sin quitarlo de la cola.
    public Optional<BookResponse> getNextBookToLoan() {
        Book next = loanQueue.peek();
        return next != null ? Optional.of(BookMapper.toResponse(next)) : Optional.empty();
    }

    // Extrae el libro prioritario y crea su préstamo activo.
    public boolean lendNextBook(String user) {
        Book book = loanQueue.poll();
        if (book == null) return false;
        registerLoan(user, book);
        return true;
    }

    // Persiste un préstamo activo para el usuario normalizado.
    public void registerLoan(String user, Book book) {
        String normalizedUser = normalizeUser(user);
        loanRepository.save(new Loan(normalizedUser, book, LocalDateTime.now(), LoanStatus.ACTIVE));
    }

    // Marca como devuelto el préstamo activo del usuario y libro indicados.
    public boolean returnBook(String user, Integer bookId) {
        String normalizedUser = normalizeUser(user);
        Optional<Loan> loan = loanRepository.findByUserAndBook_IdAndStatus(
                normalizedUser, bookId, LoanStatus.ACTIVE);
        if (!loan.isPresent()) return false;

        Loan activeLoan = loan.get();
        activeLoan.setReturnDate(LocalDateTime.now());
        activeLoan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(activeLoan);
        return true;
    }

    // Devuelve los préstamos activos agrupados por usuario.
    public Map<String, List<BookResponse>> getLoans() {
        Map<String, List<BookResponse>> result = new HashMap<>();
        loanRepository.findByStatusOrderByLoanDateAsc(LoanStatus.ACTIVE)
                .forEach(loan -> result.computeIfAbsent(loan.getUser(), key -> new ArrayList<>())
                        .add(BookMapper.toResponse(loan.getBook())));
        return result;
    }

    // Devuelve el detalle completo de todos los préstamos activos.
    public List<LoanResponse> getLoanDetails() {
        return loanRepository.findByStatusOrderByLoanDateAsc(LoanStatus.ACTIVE)
                .stream()
                .map(LoanMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Normaliza el usuario para evitar registros duplicados por formato.
    private String normalizeUser(String user) {
        return user.trim().toLowerCase();
    }
}

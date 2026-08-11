package com.libraryapp.service;

import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.mapper.BookMapper;
import com.libraryapp.model.Book;
import com.libraryapp.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

@Service
public class LibraryService {

    private final BookRepository bookRepository;

    // Req 4: cola de préstamos con prioridad por año (más nuevos primero)
    private final PriorityQueue<Book> loanQueue;
    // Req 5: registro de préstamos usuario → libros
    private final HashMap<String, List<Book>> loans;

    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.loanQueue = new PriorityQueue<>(Comparator.comparing(Book::getYear).reversed());
        this.loans = new HashMap<>();
    }

    /* ── Req 1 y 2: CRUD de libros ─────────────────────────────────────── */

    public BookResponse addBook(BookRequest request) {
        Book saved = bookRepository.save(BookMapper.toEntity(request));
        return BookMapper.toResponse(saved);
    }

    public List<BookResponse> getAllBooks() {
        return BookMapper.toResponseList(bookRepository.findAll());
    }

    public Optional<BookResponse> getBook(Integer id) {
        return bookRepository.findById(id).map(BookMapper::toResponse);
    }

    public Optional<BookResponse> updateBook(Integer id, UpdateBookRequest request) {
        return bookRepository.findById(id).map(existing -> {
            BookMapper.updateEntity(existing, request);
            return BookMapper.toResponse(bookRepository.save(existing));
        });
    }

    public boolean removeBook(Integer id) {
        if (!bookRepository.existsById(id)) return false;
        bookRepository.deleteById(id);
        return true;
    }

    public Optional<BookResponse> searchByTitle(String title) {
        return bookRepository.findByTitleIgnoreCase(title).map(BookMapper::toResponse);
    }

    @Transactional
    public void deleteByTitle(String title) {
        bookRepository.deleteByTitleIgnoreCase(title);
    }

    public List<BookResponse> getAllBooksSortedByTitle() {
        return BookMapper.toResponseList(
                bookRepository.findAll(org.springframework.data.domain.Sort.by("title")));
    }

    public List<BookResponse> getAllBooksSortedByYear() {
        return BookMapper.toResponseList(
                bookRepository.findAll(org.springframework.data.domain.Sort.by("year")));
    }

    /* ── Req 3: autores únicos en orden alfabético ──────────────────────── */

    public List<String> getAuthorsSorted() {
        return bookRepository.findDistinctAuthorsOrdered();
    }

    /* ── Req 4: cola de préstamos ───────────────────────────────────────── */

    // Optional.empty() = libro no encontrado; true = agregado; false = ya estaba
    public Optional<Boolean> addToLoanQueue(Integer bookId) {
        return bookRepository.findById(bookId).map(book -> {
            if (loanQueue.contains(book)) return false;
            loanQueue.offer(book);
            return true;
        });
    }

    public Optional<BookResponse> getNextBookToLoan() {
        Book next = loanQueue.peek();
        return next != null ? Optional.of(BookMapper.toResponse(next)) : Optional.empty();
    }

    public boolean lendNextBook(String user) {
        Book book = loanQueue.poll();
        if (book == null) return false;
        registerLoan(user, book);
        return true;
    }

    /* ── Req 5: registro de préstamos ───────────────────────────────────── */

    public void registerLoan(String user, Book book) {
        user = user.trim().toLowerCase();
        loans.computeIfAbsent(user, k -> new ArrayList<>()).add(book);
    }

    public boolean returnBook(String user, Integer bookId) {
        user = user.trim().toLowerCase();
        List<Book> userBooks = loans.get(user);
        if (userBooks == null) return false;
        boolean removed = userBooks.removeIf(b -> b.getId().equals(bookId));
        if (userBooks.isEmpty()) loans.remove(user);
        return removed;
    }

    public Map<String, List<BookResponse>> getLoans() {
        Map<String, List<BookResponse>> result = new HashMap<>();
        loans.forEach((user, books) -> result.put(user, BookMapper.toResponseList(books)));
        return result;
    }
}



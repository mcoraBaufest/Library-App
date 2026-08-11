package com.libraryapp.service;

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

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBook(Integer id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> updateBook(Integer id, Book updated) {
        return bookRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setAuthor(updated.getAuthor());
            existing.setYear(updated.getYear());
            return bookRepository.save(existing);
        });
    }

    public boolean removeBook(Integer id) {
        if (!bookRepository.existsById(id)) return false;
        bookRepository.deleteById(id);
        return true;
    }

    public Optional<Book> searchByTitle(String title) {
        return bookRepository.findByTitleIgnoreCase(title);
    }

    @Transactional
    public void deleteByTitle(String title) {
        bookRepository.deleteByTitleIgnoreCase(title);
    }

    public List<Book> getAllBooksSortedByTitle() {
        return bookRepository.findAll(org.springframework.data.domain.Sort.by("title"));
    }

    public List<Book> getAllBooksSortedByYear() {
        return bookRepository.findAll(org.springframework.data.domain.Sort.by("year"));
    }

    /* ── Req 3: autores únicos en orden alfabético ──────────────────────── */

    public List<String> getAuthorsSorted() {
        return bookRepository.findDistinctAuthorsOrdered();
    }

    /* ── Req 4: cola de préstamos ───────────────────────────────────────── */

    public boolean addToLoanQueue(Book book) {
        if (loanQueue.contains(book)) return false;
        loanQueue.offer(book);
        return true;
    }

    public Book getNextBookToLoan() {
        return loanQueue.peek();
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

    public Map<String, List<Book>> getLoans() {
        return new HashMap<>(loans);
    }
}


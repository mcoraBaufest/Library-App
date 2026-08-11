package com.libraryapp.controller;

import com.libraryapp.model.Book;
import com.libraryapp.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class BookController {

    private final LibraryService libraryService;

    public BookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /* ── Req 1 y 2: CRUD básico ────────────────────────────────────────── */

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(libraryService.addBook(book));
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Integer id) {
        return libraryService.getBook(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        return libraryService.updateBook(id, book)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        return libraryService.removeBook(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /* ── Req 3: autores únicos ─────────────────────────────────────────── */

    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAuthors() {
        return ResponseEntity.ok(libraryService.getAuthorsSorted());
    }

    /* ── Req 4: cola de préstamos ──────────────────────────────────────── */

    @PostMapping("/queue/{id}")
    public ResponseEntity<String> addToQueue(@PathVariable Integer id) {
        return libraryService.getBook(id).map(book -> {
            boolean added = libraryService.addToLoanQueue(book);
            return added
                    ? ResponseEntity.ok("Libro agregado a la cola")
                    : ResponseEntity.<String>ok("El libro ya está en la cola");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/queue/next")
    public ResponseEntity<Book> getNextInQueue() {
        Book next = libraryService.getNextBookToLoan();
        return next != null ? ResponseEntity.ok(next) : ResponseEntity.noContent().build();
    }

    @PostMapping("/queue/lend")
    public ResponseEntity<String> lendNextBook(@RequestParam String user) {
        boolean lent = libraryService.lendNextBook(user);
        return lent
                ? ResponseEntity.ok("Préstamo registrado para: " + user)
                : ResponseEntity.<String>noContent().build();
    }

    /* ── Req 5: registro de préstamos ──────────────────────────────────── */

    @GetMapping("/loans")
    public ResponseEntity<Map<String, List<Book>>> getLoans() {
        return ResponseEntity.ok(libraryService.getLoans());
    }

    @DeleteMapping("/loans/{user}/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable String user, @PathVariable Integer bookId) {
        boolean returned = libraryService.returnBook(user, bookId);
        return returned
                ? ResponseEntity.ok("Libro devuelto")
                : ResponseEntity.notFound().build();
    }
}

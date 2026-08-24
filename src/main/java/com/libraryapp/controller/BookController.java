package com.libraryapp.controller;

import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.exceptions.InvalidRequestException;
import com.libraryapp.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.LinkedHashMap;
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
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest request,
                                                BindingResult validationResult) {
        validateRequest(validationResult);
        return ResponseEntity.status(201).body(libraryService.addBook(request));
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Integer id) { //PathVariable indica que el valor del parámetro id se obtiene de la URL.
        return libraryService.getBook(id)
                .map(ResponseEntity::ok) //si encuentra , devuelve y 200 ok
                .orElse(ResponseEntity.notFound().build()); //sino devuelve 404 not found
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Integer id,
                                                   @Valid @RequestBody UpdateBookRequest request,
                                                   BindingResult validationResult) {
        validateRequest(validationResult);
        return libraryService.updateBook(id, request)
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
        return libraryService.addToLoanQueue(id)
                .map(added -> added
                ? ResponseEntity.status(201).body("Libro agregado a la cola")
                        : ResponseEntity.<String>ok("El libro ya está en la cola"))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/queue/next")
    public ResponseEntity<BookResponse> getNextInQueue() {
        return libraryService.getNextBookToLoan()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/queue/lend")
    public ResponseEntity<String> lendNextBook(@RequestParam String user) {
        boolean lent = libraryService.lendNextBook(user);
        return lent
                ? ResponseEntity.ok("Préstamo registrado para: " + user)
                : ResponseEntity.<String>noContent().build();
    }

    /* ── Req 5: registro de préstamos ──────────────────────────────────── */

    @GetMapping("/loans") //devuelve un mapa de usuarios y sus libros prestados
    public ResponseEntity<Map<String, List<BookResponse>>> getLoans() {
        return ResponseEntity.ok(libraryService.getLoans());
    }

    @DeleteMapping("/loans/{user}/{bookId}")//devuelve un 200 si se devolvió el libro, o un 404 si no se encontró el préstamo.
    public ResponseEntity<String> returnBook(@PathVariable String user, @PathVariable Integer bookId) {
        boolean returned = libraryService.returnBook(user, bookId);
        return returned
                ? ResponseEntity.ok("Libro devuelto")
                : ResponseEntity.notFound().build();
    }

    private void validateRequest(BindingResult validationResult) {
        if (!validationResult.hasErrors()) return; //si no hay errores sale

        /*Si hay errores, crea una linkedHashMap para guardarlas en orden
        obtiene todos los errores
        los recorre
        obtiene el nombre del campo y el mensaje de error */
        Map<String, String> errors = new LinkedHashMap<>();
        validationResult.getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        throw new InvalidRequestException(errors);
    }
}


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


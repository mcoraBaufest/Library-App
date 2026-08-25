package com.libraryapp.controller;

import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.dto.loan.response.LoanResponse;
import com.libraryapp.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
public class LoanController {

    private final LoanService loanService;

    // Recibe el servicio que contiene la lógica de préstamos.
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Agrega un libro a la cola de prioridad.
    @PostMapping("/queue/{id}")
    public ResponseEntity<String> addToQueue(@PathVariable Integer id) {
        return loanService.addToLoanQueue(id)
                .map(added -> added
                        ? ResponseEntity.status(201).body("Libro agregado a la cola")
                        : ResponseEntity.<String>ok("El libro ya está en la cola"))
                .orElse(ResponseEntity.notFound().build());
    }

    // Consulta el siguiente libro de la cola sin retirarlo.
    @GetMapping("/queue/next")
    public ResponseEntity<BookResponse> getNextInQueue() {
        return loanService.getNextBookToLoan()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Presta el siguiente libro de la cola al usuario indicado.
    @PostMapping("/queue/lend")
    public ResponseEntity<String> lendNextBook(@RequestParam String user) {
        boolean lent = loanService.lendNextBook(user);
        return lent
                ? ResponseEntity.ok("Préstamo registrado para: " + user)
                : ResponseEntity.<String>noContent().build();
    }

    // Devuelve los préstamos activos agrupados por usuario.
    @GetMapping("/loans")
    public ResponseEntity<Map<String, List<BookResponse>>> getLoans() {
        return ResponseEntity.ok(loanService.getLoans());
    }

    // Devuelve el detalle completo de los préstamos activos.
    @GetMapping("/loan-records")
    public ResponseEntity<List<LoanResponse>> getLoanDetails() {
        return ResponseEntity.ok(loanService.getLoanDetails());
    }

    // Registra la devolución de un libro prestado.
    @DeleteMapping("/loans/{user}/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable String user, @PathVariable Integer bookId) {
        boolean returned = loanService.returnBook(user, bookId);
        return returned
                ? ResponseEntity.ok("Libro devuelto")
                : ResponseEntity.notFound().build();
    }
}

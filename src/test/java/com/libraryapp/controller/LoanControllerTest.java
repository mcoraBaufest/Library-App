package com.libraryapp.controller;

import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.dto.loan.response.LoanResponse;
import com.libraryapp.enums.LoanStatus;
import com.libraryapp.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Carga solo la capa MVC y el controlador de préstamos.
@WebMvcTest(LoanController.class)
// Permite probar rutas sin que la autenticación interfiera.
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {

        // Simula peticiones HTTP sin levantar un servidor real.
    @Autowired
    private MockMvc mockMvc;

        // Aísla el controller de la lógica real del servicio.
    @MockBean
    private LoanService loanService;

    // Verifica que POST /queue/{id} agregue un libro a la cola.
    @Test
    void shouldAddBookToQueue() throws Exception {
        when(loanService.addToLoanQueue(1)).thenReturn(Optional.of(true));//el resultado existe y su valor es true

        mockMvc.perform(post("/queue/1"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Libro agregado a la cola"));
    }

    // Verifica que POST /queue/{id} informe si el libro ya estaba en la cola.
    @Test
    void shouldReportBookAlreadyInQueue() throws Exception {
        when(loanService.addToLoanQueue(1)).thenReturn(Optional.of(false));

        mockMvc.perform(post("/queue/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("El libro ya está en la cola"));
    }

    // Verifica que POST /queue/{id} devuelva 404 si el libro no existe.
    @Test
    void shouldReturnNotFoundWhenAddingMissingBookToQueue() throws Exception {
        when(loanService.addToLoanQueue(1)).thenReturn(Optional.empty());

        mockMvc.perform(post("/queue/1"))
                .andExpect(status().isNotFound());
    }

    // Verifica que GET /queue/next devuelva el siguiente libro.
    @Test
    void shouldGetNextBookInQueue() throws Exception {
        when(loanService.getNextBookToLoan())
                .thenReturn(Optional.of(bookResponse(1, "Clean Code", "Robert C. Martin", 2008)));

        mockMvc.perform(get("/queue/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // Verifica que GET /queue/next devuelva 204 con la cola vacía.
    @Test
    void shouldReturnNoContentWhenQueueIsEmpty() throws Exception {
        when(loanService.getNextBookToLoan()).thenReturn(Optional.empty());

        mockMvc.perform(get("/queue/next"))
                .andExpect(status().isNoContent());
    }

    // Verifica que POST /queue/lend registre un préstamo exitoso.
    @Test
    void shouldLendNextBook() throws Exception {
        when(loanService.lendNextBook("juan")).thenReturn(true);

        mockMvc.perform(post("/queue/lend").param("user", "juan"))
                .andExpect(status().isOk())
                .andExpect(content().string("Préstamo registrado para: juan"));
    }

    // Verifica que POST /queue/lend devuelva 204 si no hay libros.
    @Test
    void shouldReturnNoContentWhenLendingEmptyQueue() throws Exception {
        when(loanService.lendNextBook("juan")).thenReturn(false);

        mockMvc.perform(post("/queue/lend").param("user", "juan"))
                .andExpect(status().isNoContent());
    }

    // Verifica que GET /loans devuelva los préstamos agrupados por usuario.
    @Test
    void shouldGetLoans() throws Exception {
        Map<String, List<BookResponse>> loans = new HashMap<>();
        loans.put("juan", Collections.singletonList(
                bookResponse(1, "Clean Code", "Robert C. Martin", 2008)));
        when(loanService.getLoans()).thenReturn(loans);

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.juan[0].title").value("Clean Code"));
    }

    // Verifica que GET /loan-records devuelva el detalle de los préstamos activos.
    @Test
    void shouldGetLoanDetails() throws Exception {
        LoanResponse response = new LoanResponse(1, "juan",
                bookResponse(1, "Clean Code", "Robert C. Martin", 2008),
                LocalDateTime.of(2026, 8, 25, 10, 30), null, LoanStatus.ACTIVE);
        when(loanService.getLoanDetails()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/loan-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user").value("juan"))
                .andExpect(jsonPath("$[0].book.title").value("Clean Code"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // Verifica que DELETE /loans/{user}/{bookId} confirme una devolución.
    @Test
    void shouldReturnBook() throws Exception {
        when(loanService.returnBook("juan", 1)).thenReturn(true);

        mockMvc.perform(delete("/loans/juan/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Libro devuelto"));
    }

    // Verifica que DELETE /loans/{user}/{bookId} devuelva 404 si no existe.
    @Test
    void shouldReturnNotFoundWhenReturningMissingBook() throws Exception {
        when(loanService.returnBook("juan", 1)).thenReturn(false);

        mockMvc.perform(delete("/loans/juan/1"))
                .andExpect(status().isNotFound());
    }

    private BookResponse bookResponse(Integer id, String title, String author, int year) {
        return new BookResponse(id, title, author, year);
    }
}

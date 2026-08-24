package com.libraryapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Year;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Carga solo la capa MVC y el controlador que se quiere probar.
@WebMvcTest(BookController.class)
// Crea MockMvc y desactiva los filtros de seguridad para probar las rutas aisladas.
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

	// Inyecta la herramienta para simular peticiones HTTP sin levantar un servidor.
    @Autowired
    private MockMvc mockMvc;

	// Inyecta el conversor usado para transformar objetos Java en JSON.
    @Autowired
    private ObjectMapper objectMapper;

	// Crea un mock del service para aislar el comportamiento del controlador.
    @MockBean
    private LibraryService libraryService;

    // Verifica que POST /books cree un libro y devuelva sus datos.
    @Test
    void shouldAddBook() throws Exception {
	BookRequest request = bookRequest("Clean Code", "Robert C. Martin", 2008);
	BookResponse response = bookResponse(1, "Clean Code", "Robert C. Martin", 2008);
	when(libraryService.addBook(any(BookRequest.class))).thenReturn(response);

	mockMvc.perform(post("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // Verifica que POST /books rechace campos de texto vacíos.
    @Test
    void shouldRejectBookWithBlankFields() throws Exception {
	BookRequest request = bookRequest(" ", "", 2008);

	mockMvc.perform(post("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors.title").value("El título es obligatorio"))
		.andExpect(jsonPath("$.errors.author").value("El autor es obligatorio"));
    }

    // Verifica que POST /books rechace años fuera del rango permitido.
    @Test
    void shouldRejectBookWithInvalidYear() throws Exception {
	BookRequest request = bookRequest("Clean Code", "Robert C. Martin", 1400);

	mockMvc.perform(post("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors.year")
			.value("El año debe ser igual o posterior a 1450"));
    }

    // Verifica que POST /books rechace años posteriores al año actual.
    @Test
    void shouldRejectBookWithFutureYear() throws Exception {
	BookRequest request = bookRequest("Clean Code", "Robert C. Martin", Year.now().getValue() + 1);

	mockMvc.perform(post("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors.year")
			.value("El año no puede ser posterior al año actual"));
    }

    // Verifica que GET /books devuelva la lista de libros.
    @Test
    void shouldGetAllBooks() throws Exception {
	when(libraryService.getAllBooks()).thenReturn(Arrays.asList(
		bookResponse(1, "Clean Code", "Robert C. Martin", 2008),
		bookResponse(2, "Refactoring", "Martin Fowler", 2018)));

	mockMvc.perform(get("/books"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].title").value("Clean Code"))
		.andExpect(jsonPath("$[1].title").value("Refactoring"));
    }

    // Verifica que GET /books/{id} devuelva un libro existente.
    @Test
    void shouldGetBookWhenItExists() throws Exception {
	when(libraryService.getBook(1)).thenReturn(Optional.of(
		bookResponse(1, "Clean Code", "Robert C. Martin", 2008)));

	mockMvc.perform(get("/books/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // Verifica que GET /books/{id} devuelva 404 si el libro no existe.
    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
	when(libraryService.getBook(1)).thenReturn(Optional.empty());

	mockMvc.perform(get("/books/1"))
		.andExpect(status().isNotFound());
    }

    // Verifica que PUT /books/{id} actualice y devuelva el libro.
    @Test
    void shouldUpdateBook() throws Exception {
	UpdateBookRequest request = updateRequest("Effective Java", "Joshua Bloch", 2018);
	BookResponse response = bookResponse(1, "Effective Java", "Joshua Bloch", 2018);
	when(libraryService.updateBook(eq(1), any(UpdateBookRequest.class)))
		.thenReturn(Optional.of(response));

	mockMvc.perform(put("/books/1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("Effective Java"));
    }

    // Verifica que PUT /books/{id} devuelva 404 si no encuentra el libro.
    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBook() throws Exception {
	when(libraryService.updateBook(eq(1), any(UpdateBookRequest.class)))
		.thenReturn(Optional.empty());

	mockMvc.perform(put("/books/1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest(
				"Effective Java", "Joshua Bloch", 2018))))
		.andExpect(status().isNotFound());
    }

    // Verifica que DELETE /books/{id} devuelva 204 al eliminar correctamente.
    @Test
    void shouldDeleteBook() throws Exception {
	when(libraryService.removeBook(1)).thenReturn(true);

	mockMvc.perform(delete("/books/1"))
		.andExpect(status().isNoContent());
    }

    // Verifica que DELETE /books/{id} devuelva 404 si no elimina ningún libro.
    @Test
    void shouldReturnNotFoundWhenDeletingMissingBook() throws Exception {
	when(libraryService.removeBook(1)).thenReturn(false);

	mockMvc.perform(delete("/books/1"))
		.andExpect(status().isNotFound());
    }

    // Verifica que GET /authors devuelva los autores ordenados.
    @Test
    void shouldGetAuthors() throws Exception {
	when(libraryService.getAuthorsSorted())
		.thenReturn(Arrays.asList("Alpha Author", "Zeta Author"));

	mockMvc.perform(get("/authors"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0]").value("Alpha Author"))
		.andExpect(jsonPath("$[1]").value("Zeta Author"));
    }

    // Verifica que POST /queue/{id} agregue un libro a la cola.
    @Test
    void shouldAddBookToQueue() throws Exception {
	when(libraryService.addToLoanQueue(1)).thenReturn(Optional.of(true));

	mockMvc.perform(post("/queue/1"))
		.andExpect(status().isCreated())
		.andExpect(content().string("Libro agregado a la cola"));
    }

    // Verifica que POST /queue/{id} devuelva 404 si el libro no existe.
    @Test
    void shouldReturnNotFoundWhenAddingMissingBookToQueue() throws Exception {
	when(libraryService.addToLoanQueue(1)).thenReturn(Optional.empty());

	mockMvc.perform(post("/queue/1"))
		.andExpect(status().isNotFound());
    }

    // Verifica el mensaje de POST /queue/{id} cuando el libro ya está agregado.
    @Test
    void shouldReportBookAlreadyInQueue() throws Exception {
	when(libraryService.addToLoanQueue(1)).thenReturn(Optional.of(false));

	mockMvc.perform(post("/queue/1"))
		.andExpect(status().isOk())
		.andExpect(content().string("El libro ya está en la cola"));
    }

    // Verifica que GET /queue/next devuelva el siguiente libro.
    @Test
    void shouldGetNextBookInQueue() throws Exception {
	when(libraryService.getNextBookToLoan())
		.thenReturn(Optional.of(bookResponse(1, "Clean Code", "Robert C. Martin", 2008)));

	mockMvc.perform(get("/queue/next"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // Verifica que GET /queue/next devuelva 204 cuando la cola está vacía.
    @Test
    void shouldReturnNoContentWhenQueueIsEmpty() throws Exception {
	when(libraryService.getNextBookToLoan()).thenReturn(Optional.empty());

	mockMvc.perform(get("/queue/next"))
		.andExpect(status().isNoContent());
    }

    // Verifica que POST /queue/lend registre un préstamo exitoso.
    @Test
    void shouldLendNextBook() throws Exception {
	when(libraryService.lendNextBook("juan")).thenReturn(true);

	mockMvc.perform(post("/queue/lend").param("user", "juan"))
		.andExpect(status().isOk())
		.andExpect(content().string("Préstamo registrado para: juan"));
    }

    // Verifica que POST /queue/lend devuelva 204 si no hay libros para prestar.
    @Test
    void shouldReturnNoContentWhenLendingEmptyQueue() throws Exception {
	when(libraryService.lendNextBook("juan")).thenReturn(false);

	mockMvc.perform(post("/queue/lend").param("user", "juan"))
		.andExpect(status().isNoContent());
    }

    // Verifica que GET /loans devuelva los préstamos registrados.
    @Test
    void shouldGetLoans() throws Exception {
	Map<String, List<BookResponse>> loans = new HashMap<>();
	loans.put("juan", Collections.singletonList(
		bookResponse(1, "Clean Code", "Robert C. Martin", 2008)));
	when(libraryService.getLoans()).thenReturn(loans);

	mockMvc.perform(get("/loans"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.juan[0].title").value("Clean Code"));
    }

    // Verifica que DELETE /loans/{user}/{bookId} devuelva 200 al devolver un libro.
    @Test
    void shouldReturnBook() throws Exception {
	when(libraryService.returnBook("juan", 1)).thenReturn(true);

	mockMvc.perform(delete("/loans/juan/1"))
		.andExpect(status().isOk())
		.andExpect(content().string("Libro devuelto"));
    }

    // Verifica que DELETE /loans/{user}/{bookId} devuelva 404 si no existe el préstamo.
    @Test
    void shouldReturnNotFoundWhenReturningMissingBook() throws Exception {
	when(libraryService.returnBook("juan", 1)).thenReturn(false);

	mockMvc.perform(delete("/loans/juan/1"))
		.andExpect(status().isNotFound());
    }

    private BookRequest bookRequest(String title, String author, int year) {
	BookRequest request = new BookRequest();
	request.setTitle(title);
	request.setAuthor(author);
	request.setYear(year);
	return request;
    }

    private UpdateBookRequest updateRequest(String title, String author, int year) {
	UpdateBookRequest request = new UpdateBookRequest();
	request.setTitle(title);
	request.setAuthor(author);
	request.setYear(year);
	return request;
    }

    private BookResponse bookResponse(Integer id, String title, String author, int year) {
	return new BookResponse(id, title, author, year);
    }
}

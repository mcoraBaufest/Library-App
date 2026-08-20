package com.libraryapp.service;

import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.model.Book;
import com.libraryapp.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	/*es una anotación de Mockito que crea automáticamente la clase
    que estás probando e inyecta dentro sus dependencias simuladas.*/
	@InjectMocks
	private LibraryService libraryService;

	// Verifica que un libro se guarde y se devuelva como respuesta.
	@Test
	void shouldAddBook() {
		BookRequest request = bookRequest("Clean Code", "Robert C. Martin", 2008);
		Book savedBook = book("Clean Code", "Robert C. Martin", 2008, 1);
		when(bookRepository.save(any(Book.class))).thenReturn(savedBook); // prepara el comportamiento del repository.

		BookResponse response = libraryService.addBook(request);

		assertEquals(1, response.getId());
		assertEquals("Clean Code", response.getTitle());
		verify(bookRepository).save(any(Book.class)); //confirma que el service usó el repository.
	}

	// Verifica que se obtengan todos los libros convertidos a respuestas.
	@Test
	void shouldGetAllBooks() {
		Book firstBook = book("Clean Code", "Robert C. Martin", 2008, 1);
		Book secondBook = book("Refactoring", "Martin Fowler", 2018, 2);
		when(bookRepository.findAll()).thenReturn(Arrays.asList(firstBook, secondBook));

		List<BookResponse> responses = libraryService.getAllBooks();

		assertEquals(2, responses.size());
		assertEquals("Clean Code", responses.get(0).getTitle());
		assertEquals("Refactoring", responses.get(1).getTitle());
	}

	// Verifica que se devuelva un libro cuando existe.
	@Test
	void shouldGetBookWhenItExists() {
		when(bookRepository.findById(1))
				.thenReturn(Optional.of(book("Clean Code", "Robert C. Martin", 2008, 1)));

		Optional<BookResponse> response = libraryService.getBook(1);

		assertTrue(response.isPresent());
		assertEquals("Clean Code", response.get().getTitle());
	}

	// Verifica que la búsqueda devuelva vacío cuando el libro no existe.
	@Test
	void shouldReturnEmptyWhenBookDoesNotExist() {
		when(bookRepository.findById(1)).thenReturn(Optional.empty());

		assertFalse(libraryService.getBook(1).isPresent());
	}

	// Verifica que se actualicen los datos de un libro existente.
	@Test
	void shouldUpdateExistingBook() {
		Book existingBook = book("Clean Code", "Robert C. Martin", 2008, 1);
		UpdateBookRequest request = updateRequest("Effective Java", "Joshua Bloch", 2018);
		when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));
		when(bookRepository.save(existingBook)).thenReturn(existingBook);

		Optional<BookResponse> response = libraryService.updateBook(1, request);

		assertTrue(response.isPresent());
		assertEquals("Effective Java", response.get().getTitle());
		assertEquals("Joshua Bloch", response.get().getAuthor());
		assertEquals(2018, response.get().getYear());
		verify(bookRepository).save(existingBook);
	}

	// Verifica que un libro inexistente no pueda actualizarse.
	@Test
	void shouldNotUpdateBookWhenItDoesNotExist() {
		when(bookRepository.findById(1)).thenReturn(Optional.empty());

		Optional<BookResponse> response = libraryService.updateBook(
				1, updateRequest("Effective Java", "Joshua Bloch", 2018));

		assertFalse(response.isPresent());
	}

	// Verifica que se elimine un libro existente.
	@Test
	void shouldRemoveExistingBook() {
		when(bookRepository.existsById(1)).thenReturn(true);

		assertTrue(libraryService.removeBook(1));

		verify(bookRepository).deleteById(1);
	}

	// Verifica que no se elimine un libro inexistente.
	@Test
	void shouldNotRemoveBookWhenItDoesNotExist() {
		when(bookRepository.existsById(1)).thenReturn(false);

		assertFalse(libraryService.removeBook(1));
	}

	// Verifica la búsqueda de libros ignorando mayúsculas y minúsculas.
	@Test
	void shouldSearchBookByTitle() {
		when(bookRepository.findByTitleIgnoreCase("clean code"))
				.thenReturn(Optional.of(book("Clean Code", "Robert C. Martin", 2008, 1)));

		Optional<BookResponse> response = libraryService.searchByTitle("clean code");

		assertTrue(response.isPresent());
		assertEquals(1, response.get().getId());
	}

	// Verifica que se solicite al repositorio eliminar por título.
	@Test
	void shouldDeleteBookByTitle() {
		libraryService.deleteByTitle("Clean Code");

		verify(bookRepository).deleteByTitleIgnoreCase("Clean Code");
	}

	// Verifica que el service solicite los libros ordenados por título.
	@Test
	void shouldReturnBooksSortedByTitle() {
		List<Book> books = Arrays.asList(
				book("Clean Code", "Robert C. Martin", 2008, 1),
				book("Refactoring", "Martin Fowler", 2018, 2));
		when(bookRepository.findAll(any(Sort.class))).thenReturn(books);

		List<BookResponse> responses = libraryService.getAllBooksSortedByTitle();

		assertEquals(2, responses.size());
		verify(bookRepository).findAll(Sort.by("title"));
	}

	// Verifica que se devuelvan los autores en el orden recibido del repositorio.
	@Test
	void shouldReturnAuthorsSorted() {
		when(bookRepository.findDistinctAuthorsOrdered())
				.thenReturn(Arrays.asList("Alpha Author", "Zeta Author"));

		List<String> authors = libraryService.getAuthorsSorted();

		assertEquals(Arrays.asList("Alpha Author", "Zeta Author"), authors);
	}

	// Verifica que un libro existente se agregue a la cola de préstamos.
	@Test
	void shouldAddBookToLoanQueue() {
		when(bookRepository.findById(1))
				.thenReturn(Optional.of(book("Clean Code", "Robert C. Martin", 2008, 1)));

		Optional<Boolean> result = libraryService.addToLoanQueue(1);

		assertTrue(result.isPresent());
		assertTrue(result.get());
	}

	// Verifica que la cola priorice el libro con el año más reciente.
	@Test
	void shouldReturnNewestBookFirstFromLoanQueue() {
		Book oldBook = book("Old book", "Author", 1990, 1);
		Book newBook = book("New book", "Author", 2020, 2);
		when(bookRepository.findById(1)).thenReturn(Optional.of(oldBook));
		when(bookRepository.findById(2)).thenReturn(Optional.of(newBook));

		libraryService.addToLoanQueue(1);
		libraryService.addToLoanQueue(2);

		Optional<BookResponse> nextBook = libraryService.getNextBookToLoan();

		assertTrue(nextBook.isPresent());
		assertEquals("New book", nextBook.get().getTitle());
	}

	// Verifica que el siguiente libro se preste al usuario normalizado.
	@Test
	void shouldLendNextBookToNormalizedUser() {
		Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
		when(bookRepository.findById(1)).thenReturn(Optional.of(book));
		libraryService.addToLoanQueue(1);

		assertTrue(libraryService.lendNextBook(" Juan "));

		Map<String, List<BookResponse>> loans = libraryService.getLoans();
		assertTrue(loans.containsKey("juan"));
		assertEquals("Clean Code", loans.get("juan").get(0).getTitle());
	}

	// Verifica que no se pueda prestar si la cola está vacía.
	@Test
	void shouldReturnFalseWhenLendingFromEmptyQueue() {
		assertFalse(libraryService.lendNextBook("juan"));
	}

	// Verifica que un usuario pueda devolver un libro prestado.
	@Test
	void shouldReturnLoanedBook() {
		Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
		libraryService.registerLoan(" JUAN ", book);

		assertTrue(libraryService.returnBook("juan", 1));
		assertTrue(libraryService.getLoans().isEmpty());
	}

	// Verifica que no se devuelva un libro que el usuario no tiene.
	@Test
	void shouldReturnFalseWhenUserHasNoLoanForBook() {
		libraryService.registerLoan("juan", book("Clean Code", "Robert C. Martin", 2008, 1));

		assertFalse(libraryService.returnBook("juan", 2));
	}

	// Verifica que no haya préstamos registrados inicialmente.
	@Test
	void shouldReturnEmptyLoansWhenThereAreNoLoans() {
		assertEquals(Collections.emptyMap(), libraryService.getLoans());
	}

	// Crea un request con los datos necesarios para agregar un libro.
	private BookRequest bookRequest(String title, String author, int year) {
		BookRequest request = new BookRequest();
		request.setTitle(title);
		request.setAuthor(author);
		request.setYear(year);
		return request;
	}

	// Crea un request con los datos necesarios para actualizar un libro.
	private UpdateBookRequest updateRequest(String title, String author, int year) {
		UpdateBookRequest request = new UpdateBookRequest();
		request.setTitle(title);
		request.setAuthor(author);
		request.setYear(year);
		return request;
	}

	// Crea un libro de prueba y le asigna un ID.
	private Book book(String title, String author, int year, int id) {
		Book book = new Book(title, author, year);
		book.setId(id);
		return book;
	}
}

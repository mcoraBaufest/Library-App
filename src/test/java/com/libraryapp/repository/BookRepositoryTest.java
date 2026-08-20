package com.libraryapp.repository;

import com.libraryapp.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest //Levanta solo la parte de Spring relacionada con JPA y usa una base H2 para las pruebas.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	@BeforeEach //Se ejecuta antes de cada test y elimina los libros existentes. Esto evita que los datos de data.sql interfieran entre pruebas.
	void cleanDatabase() {
		bookRepository.deleteAll();
	}

	/*Guarda un libro y luego
    lo busca usando el título en minúsculas.
     */
	@Test
	void shouldFindBookByTitleIgnoringCase() {
		bookRepository.save(new Book("Clean Code", "Robert C. Martin", 2008));

		Optional<Book> result = bookRepository.findByTitleIgnoreCase("clean code");

		assertTrue(result.isPresent());
		assertEquals("Clean Code", result.get().getTitle());
	}
	/*Verifica que se devuelva un Optional vacío
    cuando el título no existe. */
	@Test
	void shouldReturnEmptyWhenTitleDoesNotExist() {
		Optional<Book> result = bookRepository.findByTitleIgnoreCase("Unknown book");

		assertFalse(result.isPresent());
	}

    /*Elimina un libro usando el título en minúsculas. */
	@Test
	void shouldDeleteBookByTitleIgnoringCase() {
		bookRepository.save(new Book("Clean Code", "Robert C. Martin", 2008));

		bookRepository.deleteByTitleIgnoreCase("clean code");

		assertFalse(bookRepository.findByTitleIgnoreCase("Clean Code").isPresent());
	}

    /*Devuelve una lista de autores distintos en orden alfabético. */
	@Test
	void shouldReturnDistinctAuthorsInAlphabeticalOrder() {
		bookRepository.save(new Book("Book one", "Zeta Author", 2000));
		bookRepository.save(new Book("Book two", "Alpha Author", 2001));
		bookRepository.save(new Book("Book three", "Zeta Author", 2002));

		List<String> authors = bookRepository.findDistinctAuthorsOrdered();

		assertEquals(2, authors.size());
		assertEquals("Alpha Author", authors.get(0));
		assertEquals("Zeta Author", authors.get(1));
	}
}

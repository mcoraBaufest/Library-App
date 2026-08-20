package com.libraryapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BookEntityTest {

	@Test
	void shouldCreateBookWithConstructorValues() {
		Book book = new Book("Clean Code", "Robert C. Martin", 2008);

		assertEquals("Clean Code", book.getTitle());
		assertEquals("Robert C. Martin", book.getAuthor());
		assertEquals(2008, book.getYear());
	}

	@Test
	void shouldUpdateBookWithSetters() {
		Book book = new Book();

		book.setId(1);
		book.setTitle("Effective Java");
		book.setAuthor("Joshua Bloch");
		book.setYear(2018);

		assertEquals(1, book.getId());
		assertEquals("Effective Java", book.getTitle());
		assertEquals("Joshua Bloch", book.getAuthor());
		assertEquals(2018, book.getYear());
	}

	@Test
	void shouldConsiderBooksWithSameIdEqual() {
		Book firstBook = new Book("Clean Code", "Robert C. Martin", 2008);
		Book secondBook = new Book("Another title", "Another author", 2020);
		firstBook.setId(1);
		secondBook.setId(1);

		assertEquals(firstBook, secondBook);
		assertEquals(firstBook.hashCode(), secondBook.hashCode());
	}

	@Test
	void shouldNotConsiderBooksWithDifferentIdsEqual() {
		Book firstBook = new Book("Clean Code", "Robert C. Martin", 2008);
		Book secondBook = new Book("Clean Code", "Robert C. Martin", 2008);
		firstBook.setId(1);
		secondBook.setId(2);

		assertNotEquals(firstBook, secondBook);
	}
}

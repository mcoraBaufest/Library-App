package com.libraryapp.mapper;

import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.model.Book;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookMapperTest {

	@Test
	void shouldConvertBookRequestToEntity() {
		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setAuthor("Robert C. Martin");
		request.setYear(2008);

		Book book = BookMapper.toEntity(request);

		assertEquals("Clean Code", book.getTitle());
		assertEquals("Robert C. Martin", book.getAuthor());
		assertEquals(2008, book.getYear());
	}

	@Test
	void shouldConvertBookToResponse() {
		Book book = new Book("Clean Code", "Robert C. Martin", 2008);
		book.setId(1);

		BookResponse response = BookMapper.toResponse(book);

		assertEquals(1, response.getId());
		assertEquals("Clean Code", response.getTitle());
		assertEquals("Robert C. Martin", response.getAuthor());
		assertEquals(2008, response.getYear());
	}

	@Test
	void shouldConvertBookListToResponseList() {
		Book firstBook = new Book("Clean Code", "Robert C. Martin", 2008);
		firstBook.setId(1);
		Book secondBook = new Book("Refactoring", "Martin Fowler", 2018);
		secondBook.setId(2);

		List<BookResponse> responses = BookMapper.toResponseList(
				Arrays.asList(firstBook, secondBook));

		assertEquals(2, responses.size());
		assertEquals("Clean Code", responses.get(0).getTitle());
		assertEquals("Refactoring", responses.get(1).getTitle());
		assertEquals(2, responses.get(1).getId());
	}

	@Test
	void shouldUpdateBookWithUpdateRequest() {
		Book book = new Book("Clean Code", "Robert C. Martin", 2008);
		book.setId(1);
		UpdateBookRequest request = new UpdateBookRequest();
		request.setTitle("Effective Java");
		request.setAuthor("Joshua Bloch");
		request.setYear(2018);

		BookMapper.updateEntity(book, request);

		assertEquals(1, book.getId());
		assertEquals("Effective Java", book.getTitle());
		assertEquals("Joshua Bloch", book.getAuthor());
		assertEquals(2018, book.getYear());
	}
}

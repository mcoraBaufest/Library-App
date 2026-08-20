package com.libraryapp.dto.book.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookResponseTest {

    @Test
    void shouldCreateResponseWithConstructorValues() {
        BookResponse response = new BookResponse(1, "Clean Code", "Robert C. Martin", 2008);

        assertEquals(1, response.getId());
        assertEquals("Clean Code", response.getTitle());
        assertEquals("Robert C. Martin", response.getAuthor());
        assertEquals(2008, response.getYear());
    }

    @Test
    void shouldStoreResponseValuesWithSetters() {
        BookResponse response = new BookResponse();

        response.setId(2);
        response.setTitle("Effective Java");
        response.setAuthor("Joshua Bloch");
        response.setYear(2018);

        assertEquals(2, response.getId());
        assertEquals("Effective Java", response.getTitle());
        assertEquals("Joshua Bloch", response.getAuthor());
        assertEquals(2018, response.getYear());
    }
}

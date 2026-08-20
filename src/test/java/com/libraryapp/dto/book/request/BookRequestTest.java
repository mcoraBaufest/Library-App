package com.libraryapp.dto.book.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookRequestTest {

    @Test
    void shouldStoreBookRequestValues() {
        BookRequest request = new BookRequest();

        request.setTitle("Clean Code");
        request.setAuthor("Robert C. Martin");
        request.setYear(2008);

        assertEquals("Clean Code", request.getTitle());
        assertEquals("Robert C. Martin", request.getAuthor());
        assertEquals(2008, request.getYear());
    }
}

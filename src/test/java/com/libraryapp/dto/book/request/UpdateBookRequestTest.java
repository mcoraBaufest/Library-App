package com.libraryapp.dto.book.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateBookRequestTest {

    @Test
    void shouldStoreUpdateBookRequestValues() {
        UpdateBookRequest request = new UpdateBookRequest();

        request.setTitle("Effective Java");
        request.setAuthor("Joshua Bloch");
        request.setYear(2018);

        assertEquals("Effective Java", request.getTitle());
        assertEquals("Joshua Bloch", request.getAuthor());
        assertEquals(2018, request.getYear());
    }
}

package com.libraryapp.mapper;

import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.model.Book;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    private BookMapper() {}

    public static Book toEntity(BookRequest request) {
        return new Book(request.getTitle(), request.getAuthor(), request.getYear());
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getYear());
    }

    public static List<BookResponse> toResponseList(List<Book> books) {
        return books.stream().map(BookMapper::toResponse).collect(Collectors.toList());
    }

    public static void updateEntity(Book book, UpdateBookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setYear(request.getYear());
    }
}

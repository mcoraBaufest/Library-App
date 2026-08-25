package com.libraryapp.service;

import com.libraryapp.dto.book.request.BookRequest;
import com.libraryapp.dto.book.request.UpdateBookRequest;
import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.mapper.BookMapper;
import com.libraryapp.model.Book;
import com.libraryapp.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final BookRepository bookRepository;

    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /* ── Req 1 y 2: CRUD de libros ─────────────────────────────────────── */

    public BookResponse addBook(BookRequest request) {
        Book saved = bookRepository.save(BookMapper.toEntity(request));
        return BookMapper.toResponse(saved);
    }

    public List<BookResponse> getAllBooks() {
        return BookMapper.toResponseList(bookRepository.findAll());
    }

    //OPTIONAL indica que un resultado puede existir o no existir
    public Optional<BookResponse> getBook(Integer id) {
        return bookRepository.findById(id).map(BookMapper::toResponse);
    }

    public Optional<BookResponse> updateBook(Integer id, UpdateBookRequest request) {
        return bookRepository.findById(id).map(existing -> { //solo se ejecuta si encuentra el libro
            BookMapper.updateEntity(existing, request);
            return BookMapper.toResponse(bookRepository.save(existing));
        });
    }

    public boolean removeBook(Integer id) {
        if (!bookRepository.existsById(id)) return false;
        bookRepository.deleteById(id);
        return true;
    }

    public Optional<BookResponse> searchByTitle(String title) {
        return bookRepository.findByTitleIgnoreCase(title).map(BookMapper::toResponse);
    }

    @Transactional
    public void deleteByTitle(String title) {
        bookRepository.deleteByTitleIgnoreCase(title);
    }

    public List<BookResponse> getAllBooksSortedByTitle() {
        return BookMapper.toResponseList( //el Sort.by("title") indica que se ordene por el atributo title de la entidad Book (JPA lo traduce a la columna correspondiente en la base de datos)
                bookRepository.findAll(Sort.by("title")));
    }

    public List<BookResponse> getAllBooksSortedByYear() {
        return BookMapper.toResponseList(
                bookRepository.findAll(Sort.by("year")));
    }

    /* ── Req 3: autores únicos en orden alfabético ──────────────────────── */

    public List<String> getAuthorsSorted() {
        return bookRepository.findDistinctAuthorsOrdered();
    }

}



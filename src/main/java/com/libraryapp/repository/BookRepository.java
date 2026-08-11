package com.libraryapp.repository;

import com.libraryapp.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByTitleIgnoreCase(String title);

    void deleteByTitleIgnoreCase(String title);

    // Req 3: autores únicos ordenados alfabéticamente desde la DB
    @Query("SELECT DISTINCT b.author FROM Book b ORDER BY b.author")
    List<String> findDistinctAuthorsOrdered();
}

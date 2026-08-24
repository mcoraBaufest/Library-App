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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

@Service
public class LibraryService {

    private final BookRepository bookRepository;

    // Req 4: cola de préstamos con prioridad por año (más nuevos primero)
    private final PriorityQueue<Book> loanQueue;
    // Req 5: registro de préstamos usuario → libros
    private final HashMap<String, List<Book>> loans;

    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.loanQueue = new PriorityQueue<>(Comparator.comparing(Book::getYear).reversed());
        this.loans = new HashMap<>();
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

    /* ── Req 4: cola de préstamos ───────────────────────────────────────── */

    // Optional.empty() = libro no encontrado; true = agregado; false = ya estaba
    public Optional<Boolean> addToLoanQueue(Integer bookId) {
        return bookRepository.findById(bookId).map(book -> {
            if (loanQueue.contains(book)) return false;
            loanQueue.offer(book);
            return true;
        });
    }

    public Optional<BookResponse> getNextBookToLoan() {
        Book next = loanQueue.peek(); //mira el primer elemento de la cola sin eliminarlo
        return next != null ? Optional.of(BookMapper.toResponse(next)) : Optional.empty();
        //si hay un libro lo convierte en response , si la cola esta vacia Optional.empty()
    }

    public boolean lendNextBook(String user) {
        Book book = loanQueue.poll();//elimina y devuelve el primer elemento de la cola, o null si está vacía
        if (book == null) return false; //si no hay libro para prestar devuelve false
        registerLoan(user, book); //si hay libro lo registra en el mapa de préstamos y devuelve true
        return true;
    }

    /* ── Req 5: registro de préstamos ───────────────────────────────────── */

    public void registerLoan(String user, Book book) {
        user = user.trim().toLowerCase();
        loans.computeIfAbsent(user, k -> new ArrayList<>()).add(book);
        /*si el usuario no tenía préstamos previos, se crea una nueva lista y
        se agrega el libro; Si el usuario ya existe:
        obtiene su lista de préstamos.*/
    }

    public boolean returnBook(String user, Integer bookId) {
        user = user.trim().toLowerCase(); //recibe usuario
        List<Book> userBooks = loans.get(user);//busca sus prestamos
        if (userBooks == null) return false; //si no tiene prestamos devuelve false
        boolean removed = userBooks.removeIf(
            b -> b.getId().equals(bookId));
            /*elimina el libro de la lista de prestamos del usuario y devuelve true
            si lo encontró y eliminó, o false si no lo encontró*/
        if (userBooks.isEmpty()) loans.remove(user);
        /*Si el usuario ya no tiene libros prestados, se elimina su entrada
        completa del mapa. */
        return removed;
    }

    /*Este método devuelve todos los préstamos, pero transforma
    los objetos Book internos en
    BookResponse antes de enviarlos al controller. */
    public Map<String, List<BookResponse>> getLoans() {
        Map<String, List<BookResponse>> result = new HashMap<>();
        loans.forEach((user, books) -> result.put(user, BookMapper.toResponseList(books)));
        return result;
    }
}



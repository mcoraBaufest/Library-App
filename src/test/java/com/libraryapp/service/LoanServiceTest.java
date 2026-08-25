package com.libraryapp.service;

import com.libraryapp.dto.book.response.BookResponse;
import com.libraryapp.dto.loan.response.LoanResponse;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.model.LoanStatus;
import com.libraryapp.model.User;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.LoanRepository;
import com.libraryapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Activa Mockito para aislar el servicio de sus repositorios reales.
@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    // Simula la búsqueda de libros que alimenta la cola.
    @Mock
    private BookRepository bookRepository;

    // Simula la persistencia de los préstamos.
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    // Crea el servicio real e inyecta sus dependencias simuladas.
    @InjectMocks
    private LoanService loanService;

    // Verifica que un libro existente se agregue a la cola.
    @Test
    void shouldAddBookToLoanQueue() {
        when(bookRepository.findById(1))
                .thenReturn(Optional.of(book("Clean Code", "Robert C. Martin", 2008, 1)));

        Optional<Boolean> result = loanService.addToLoanQueue(1);

        assertTrue(result.isPresent());
        assertTrue(result.get());
    }

    // Verifica que el mismo libro no se agregue dos veces.
    @Test
    void shouldNotAddSameBookToLoanQueueTwice() {
        Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        loanService.addToLoanQueue(1);
        Optional<Boolean> result = loanService.addToLoanQueue(1);

        assertTrue(result.isPresent());
        assertFalse(result.get());
    }

    // Verifica que la cola priorice el libro con año más reciente.
    @Test
    void shouldReturnNewestBookFirstFromLoanQueue() {
        Book oldBook = book("Old book", "Author", 1990, 1);
        Book newBook = book("New book", "Author", 2020, 2);
        when(bookRepository.findById(1)).thenReturn(Optional.of(oldBook));
        when(bookRepository.findById(2)).thenReturn(Optional.of(newBook));

        loanService.addToLoanQueue(1);
        loanService.addToLoanQueue(2);

        Optional<BookResponse> nextBook = loanService.getNextBookToLoan();

        assertTrue(nextBook.isPresent());
        assertEquals("New book", nextBook.get().getTitle());
    }

    // Verifica que una cola vacía no tenga siguiente libro.
    @Test
    void shouldReturnEmptyWhenLoanQueueIsEmpty() {
        assertFalse(loanService.getNextBookToLoan().isPresent());
    }

    // Verifica que el préstamo se normalice y se persista como activo.
    @Test
    void shouldLendNextBookAndPersistActiveLoan() {
        Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        User user = user("juan");
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(user));
        loanService.addToLoanQueue(1);

        assertTrue(loanService.lendNextBook(" Juan "));

        ArgumentCaptor<Loan> captor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(captor.capture());
        Loan savedLoan = captor.getValue();
        assertEquals("juan", savedLoan.getUser().getUsername());
        assertEquals(book, savedLoan.getBook());
        assertEquals(LoanStatus.ACTIVE, savedLoan.getStatus());
        assertNotNull(savedLoan.getLoanDate());
    }

    // Verifica que no se pueda prestar con la cola vacía.
    @Test
    void shouldReturnFalseWhenLendingFromEmptyQueue() {
        assertFalse(loanService.lendNextBook("juan"));
    }

    // Verifica que la devolución cambie el estado y guarde la fecha.
    @Test
    void shouldReturnLoanedBookAndMarkItReturned() {
        Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
        Loan loan = loan("juan", book, LoanStatus.ACTIVE);
        when(loanRepository.findByUser_UsernameAndBook_IdAndStatus("juan", 1, LoanStatus.ACTIVE))
                .thenReturn(Optional.of(loan));

        assertTrue(loanService.returnBook(" JUAN ", 1));

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertNotNull(loan.getReturnDate());
        verify(loanRepository).save(loan);
    }

    // Verifica que falle la devolución sin un préstamo activo.
    @Test
    void shouldReturnFalseWhenActiveLoanDoesNotExist() {
        when(loanRepository.findByUser_UsernameAndBook_IdAndStatus("juan", 1, LoanStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertFalse(loanService.returnBook("juan", 1));
    }

    // Verifica que los préstamos activos se agrupen por usuario.
    @Test
    void shouldReturnActiveLoansGroupedByUser() {
        Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
        Loan loan = loan("juan", book, LoanStatus.ACTIVE);
        when(loanRepository.findByStatusOrderByLoanDateAsc(LoanStatus.ACTIVE))
                .thenReturn(Collections.singletonList(loan));

        Map<String, List<BookResponse>> loans = loanService.getLoans();

        assertEquals(1, loans.size());
        assertEquals("Clean Code", loans.get("juan").get(0).getTitle());
    }

    // Verifica que se devuelva el detalle de los préstamos activos.
    @Test
    void shouldReturnActiveLoanDetails() {
        Book book = book("Clean Code", "Robert C. Martin", 2008, 1);
        Loan loan = loan("juan", book, LoanStatus.ACTIVE);
        when(loanRepository.findByStatusOrderByLoanDateAsc(LoanStatus.ACTIVE))
                .thenReturn(Arrays.asList(loan));

        List<LoanResponse> details = loanService.getLoanDetails();

        assertEquals(1, details.size());
        assertEquals("juan", details.get(0).getUser());
        assertEquals(LoanStatus.ACTIVE, details.get(0).getStatus());
    }

    // Crea un libro de prueba con un ID conocido.
    private Book book(String title, String author, int year, int id) {
        Book book = new Book(title, author, year);
        book.setId(id);
        return book;
    }

    // Crea un préstamo de prueba con fecha y estado indicados.
    private Loan loan(String user, Book book, LoanStatus status) {
        Loan loan = new Loan(new User(user, user + "@example.com"), book, LocalDateTime.now(), status);
        loan.setId(1);
        return loan;
    }

    private User user(String username) {
        User user = new User(username, username + "@example.com");
        user.setId(1);
        return user;
    }
}

package com.libraryapp.repository;

import com.libraryapp.enums.LoanStatus;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Carga JPA para probar las consultas reales contra H2.
@DataJpaTest
// Mantiene la URL del proyecto, incluida la configuración de YEAR.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoanRepositoryTest {

    // Accede a la tabla de libros usada por la relación del préstamo.
    @Autowired
    private BookRepository bookRepository;

    // Accede al repository de préstamos que se está probando.
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    // Limpia los datos antes de cada prueba para mantener aislamiento.
    @BeforeEach
    void cleanDatabase() {
        loanRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    // Verifica la búsqueda de un préstamo activo por usuario y libro.
    @Test
    void shouldFindActiveLoanByUserAndBook() {
        Book book = bookRepository.save(new Book("Clean Code", "Robert C. Martin", 2008));
        User user = userRepository.save(new User("juan", "juan@example.com"));
        Loan loan = loanRepository.save(new Loan(
            user, book, LocalDateTime.now(), LoanStatus.ACTIVE));

        Optional<Loan> result = loanRepository.findByUser_UsernameAndBook_IdAndStatus(
                "juan", book.getId(), LoanStatus.ACTIVE);

        assertTrue(result.isPresent());
        assertEquals(loan.getId(), result.get().getId());
    }

    // Verifica que solo se devuelvan préstamos activos en orden de fecha.
    @Test
    void shouldFindOnlyActiveLoansOrderedByDate() {
        Book firstBook = bookRepository.save(new Book("First", "Author", 2000));
        Book secondBook = bookRepository.save(new Book("Second", "Author", 2001));
        User juan = userRepository.save(new User("juan", "juan@example.com"));
        User maria = userRepository.save(new User("maria", "maria@example.com"));
        LocalDateTime firstDate = LocalDateTime.of(2026, 8, 25, 10, 0);
        LocalDateTime secondDate = firstDate.plusHours(1);
        loanRepository.save(new Loan(juan, firstBook, firstDate, LoanStatus.ACTIVE));
        loanRepository.save(new Loan(maria, secondBook, secondDate, LoanStatus.RETURNED));

        List<Loan> activeLoans = loanRepository.findByStatusOrderByLoanDateAsc(LoanStatus.ACTIVE);

        assertEquals(1, activeLoans.size());
        assertEquals("juan", activeLoans.get(0).getUser().getUsername());
    }
}

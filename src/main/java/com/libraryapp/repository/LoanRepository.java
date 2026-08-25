package com.libraryapp.repository;

import com.libraryapp.model.Loan;
import com.libraryapp.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer> {

    // Busca préstamos de un estado y los ordena por fecha de alta.
    List<Loan> findByStatusOrderByLoanDateAsc(LoanStatus status);

    // Busca el préstamo activo de un usuario para un libro específico.
    Optional<Loan> findByUser_UsernameAndBook_IdAndStatus(
            String username, Integer bookId, LoanStatus status);
}

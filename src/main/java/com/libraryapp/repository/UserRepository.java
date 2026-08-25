package com.libraryapp.repository;

import com.libraryapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Busca un usuario por su nombre único.
    Optional<User> findByUsername(String username);
}
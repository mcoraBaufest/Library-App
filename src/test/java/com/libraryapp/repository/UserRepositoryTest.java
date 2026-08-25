package com.libraryapp.repository;

import com.libraryapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Carga JPA para probar la consulta real de usuarios contra H2.
@DataJpaTest
// Mantiene la configuración H2 en archivo del proyecto.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    // Repository de usuarios que se está probando.
    @Autowired
    private UserRepository userRepository;

    // Limpia usuarios para aislar cada prueba.
    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    // Verifica que un usuario pueda buscarse por su nombre único.
    @Test
    void shouldFindUserByUsername() {
        User savedUser = userRepository.save(new User("juan", "juan@example.com"));

        Optional<User> result = userRepository.findByUsername("juan");

        assertTrue(result.isPresent());
        assertEquals(savedUser.getId(), result.get().getId());
        assertEquals("juan@example.com", result.get().getEmail());
    }
}

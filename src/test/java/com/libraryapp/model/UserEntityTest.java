package com.libraryapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserEntityTest {

    // Verifica que el constructor inicialice el nombre de usuario y el email.
    @Test
    void shouldCreateUserWithConstructorValues() {
        User user = new User("juan", "juan@example.com");

        assertEquals("juan", user.getUsername());
        assertEquals("juan@example.com", user.getEmail());
    }

    // Verifica que los setters actualicen los datos del usuario.
    @Test
    void shouldUpdateUserWithSetters() {
        User user = new User();

        user.setId(1);
        user.setUsername("maria");
        user.setEmail("maria@example.com");

        assertEquals(1, user.getId());
        assertEquals("maria", user.getUsername());
        assertEquals("maria@example.com", user.getEmail());
    }

    // Verifica que dos usuarios con el mismo ID representen la misma entidad.
    @Test
    void shouldConsiderUsersWithSameIdEqual() {
        User firstUser = new User("juan", "juan@example.com");
        User secondUser = new User("otro", "otro@example.com");
        firstUser.setId(1);
        secondUser.setId(1);

        assertEquals(firstUser, secondUser);
        assertEquals(firstUser.hashCode(), secondUser.hashCode());
    }

    // Verifica que usuarios con IDs diferentes sean entidades distintas.
    @Test
    void shouldNotConsiderUsersWithDifferentIdsEqual() {
        User firstUser = new User("juan", "juan@example.com");
        User secondUser = new User("juan", "juan@example.com");
        firstUser.setId(1);
        secondUser.setId(2);

        assertNotEquals(firstUser, secondUser);
    }
}

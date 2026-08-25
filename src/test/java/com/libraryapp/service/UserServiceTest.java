package com.libraryapp.service;

import com.libraryapp.dto.user.request.UserRequest;
import com.libraryapp.dto.user.response.UserResponse;
import com.libraryapp.exceptions.InvalidRequestException;
import com.libraryapp.model.User;
import com.libraryapp.repository.UserRepository;
import com.libraryapp.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

// Activa Mockito para aislar UserService del repository real.
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    // Verifica que se cree y guarde un usuario nuevo.
    @Test
    void shouldAddUser() {
        User user = user("juan", "juan@example.com", 1);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.addUser(request(" JUAN ", "JUAN@EXAMPLE.COM "));

        assertEquals(1, response.getId());
        assertEquals("juan", response.getUsername());
        verify(userRepository).save(any(User.class));
    }

    // Verifica que no se repita un nombre de usuario.
    @Test
    void shouldRejectDuplicatedUsername() {
        doThrow(new InvalidRequestException(Collections.singletonMap(
            "username", "El nombre de usuario ya está registrado")))
            .when(userValidator).validateUsernameIsAvailable("juan", null);

        assertThrows(InvalidRequestException.class,
                () -> userService.addUser(request("juan", "other@example.com")));
    }

    // Verifica que se obtengan todos los usuarios como DTOs.
    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                user("juan", "juan@example.com", 1),
                user("maria", "maria@example.com", 2)));

        assertEquals(2, userService.getAllUsers().size());
    }

    // Verifica que se devuelva un usuario existente.
    @Test
    void shouldGetUserWhenItExists() {
        when(userRepository.findById(1))
                .thenReturn(Optional.of(user("juan", "juan@example.com", 1)));

        Optional<UserResponse> response = userService.getUser(1);

        assertTrue(response.isPresent());
        assertEquals("juan", response.get().getUsername());
    }

    // Verifica que se devuelva vacío si el usuario no existe.
    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertFalse(userService.getUser(1).isPresent());
    }

    // Verifica que se actualicen los datos del usuario.
    @Test
    void shouldUpdateUser() {
        User existing = user("juan", "juan@example.com", 1);
        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        Optional<UserResponse> response = userService.updateUser(
                1, request("maria", "maria@example.com"));

        assertTrue(response.isPresent());
        assertEquals("maria", response.get().getUsername());
        verify(userRepository).save(existing);
    }

    // Verifica que un usuario inexistente no se actualice.
    @Test
    void shouldNotUpdateMissingUser() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertFalse(userService.updateUser(1, request("maria", "maria@example.com")).isPresent());
    }

    // Verifica que se elimine un usuario existente.
    @Test
    void shouldRemoveUser() {
        when(userRepository.existsById(1)).thenReturn(true);

        assertTrue(userService.removeUser(1));
        verify(userRepository).deleteById(1);
    }

    // Verifica que no se elimine un usuario inexistente.
    @Test
    void shouldNotRemoveMissingUser() {
        when(userRepository.existsById(1)).thenReturn(false);

        assertFalse(userService.removeUser(1));
    }

    private UserRequest request(String username, String email) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setEmail(email);
        return request;
    }

    private User user(String username, String email, int id) {
        User user = new User(username, email);
        user.setId(id);
        return user;
    }
}

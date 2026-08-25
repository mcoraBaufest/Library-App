package com.libraryapp.validation;

import com.libraryapp.exceptions.InvalidRequestException;
import com.libraryapp.model.User;
import com.libraryapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

// Activa Mockito para probar UserValidator sin una base de datos real.
@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    // Verifica que un username no registrado sea válido.
    @Test
    void shouldAllowAvailableUsername() {
        when(userRepository.findByUsername("juan")).thenReturn(Optional.empty());

        UserValidator validator = new UserValidator(userRepository);

        assertDoesNotThrow(() -> validator.validateUsernameIsAvailable("juan", null));
    }

    // Verifica que un username registrado sea rechazado.
    @Test
    void shouldRejectDuplicatedUsername() {
        User existing = new User("juan", "juan@example.com");
        existing.setId(1);
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(existing));

        UserValidator validator = new UserValidator(userRepository);

        assertThrows(InvalidRequestException.class,
                () -> validator.validateUsernameIsAvailable("juan", null));
    }

    // Verifica que un usuario pueda conservar su propio username al actualizarse.
    @Test
    void shouldAllowCurrentUsernameDuringUpdate() {
        User existing = new User("juan", "juan@example.com");
        existing.setId(1);
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(existing));

        UserValidator validator = new UserValidator(userRepository);

        assertDoesNotThrow(() -> validator.validateUsernameIsAvailable("juan", 1));
    }
}

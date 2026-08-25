package com.libraryapp.validation;

import com.libraryapp.exceptions.InvalidRequestException;
import com.libraryapp.model.User;
import com.libraryapp.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUsernameIsAvailable(String username, Integer currentId) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent() && !existing.get().getId().equals(currentId)) {
            throw new InvalidRequestException(Collections.singletonMap(
                    "username", "El nombre de usuario ya está registrado"));
        }
    }
}

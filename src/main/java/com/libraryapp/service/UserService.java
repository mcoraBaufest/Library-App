package com.libraryapp.service;

import com.libraryapp.dto.user.request.UserRequest;
import com.libraryapp.dto.user.response.UserResponse;
import com.libraryapp.mapper.UserMapper;
import com.libraryapp.model.User;
import com.libraryapp.repository.UserRepository;
import com.libraryapp.validation.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public UserResponse addUser(UserRequest request) {
        User user = UserMapper.toEntity(request);
        userValidator.validateUsernameIsAvailable(user.getUsername(), null);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> getUser(Integer id) {
        return userRepository.findById(id).map(UserMapper::toResponse);
    }

    public Optional<UserResponse> updateUser(Integer id, UserRequest request) {
        return userRepository.findById(id).map(existing -> {
            User updated = UserMapper.toEntity(request);
            userValidator.validateUsernameIsAvailable(updated.getUsername(), id);
            existing.setUsername(updated.getUsername());
            existing.setEmail(updated.getEmail());
            return UserMapper.toResponse(userRepository.save(existing));
        });
    }

    public boolean removeUser(Integer id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

}

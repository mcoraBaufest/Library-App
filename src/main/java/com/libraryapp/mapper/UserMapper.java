package com.libraryapp.mapper;

import com.libraryapp.dto.user.request.UserRequest;
import com.libraryapp.dto.user.response.UserResponse;
import com.libraryapp.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toEntity(UserRequest request) {
        return new User(
                request.getUsername().trim().toLowerCase(),
                request.getEmail().trim().toLowerCase());
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    public static void updateEntity(User user, UserRequest request) {
        user.setUsername(request.getUsername().trim().toLowerCase());
        user.setEmail(request.getEmail().trim().toLowerCase());
    }
}

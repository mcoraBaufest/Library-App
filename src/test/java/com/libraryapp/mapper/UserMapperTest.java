package com.libraryapp.mapper;

import com.libraryapp.dto.user.request.UserRequest;
import com.libraryapp.dto.user.response.UserResponse;
import com.libraryapp.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    void shouldConvertRequestToEntityAndNormalizeValues() {
        UserRequest request = userRequest(" JUAN ", " JUAN@EXAMPLE.COM ");

        User user = UserMapper.toEntity(request);

        assertEquals("juan", user.getUsername());
        assertEquals("juan@example.com", user.getEmail());
    }

    @Test
    void shouldConvertEntityToResponse() {
        User user = new User("juan", "juan@example.com");
        user.setId(1);

        UserResponse response = UserMapper.toResponse(user);

        assertEquals(1, response.getId());
        assertEquals("juan", response.getUsername());
        assertEquals("juan@example.com", response.getEmail());
    }

    @Test
    void shouldUpdateEntityWithRequestValues() {
        User user = new User("juan", "juan@example.com");

        UserMapper.updateEntity(user, userRequest(" MARIA ", " MARIA@EXAMPLE.COM "));

        assertEquals("maria", user.getUsername());
        assertEquals("maria@example.com", user.getEmail());
    }

    private UserRequest userRequest(String username, String email) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setEmail(email);
        return request;
    }
}

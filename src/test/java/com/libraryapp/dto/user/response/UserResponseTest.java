package com.libraryapp.dto.user.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserResponseTest {

    @Test
    void shouldCreateResponseWithConstructorValues() {
        UserResponse response = new UserResponse(1, "juan", "juan@example.com");

        assertEquals(1, response.getId());
        assertEquals("juan", response.getUsername());
        assertEquals("juan@example.com", response.getEmail());
    }

    @Test
    void shouldStoreValuesWithSetters() {
        UserResponse response = new UserResponse();
        response.setId(2);
        response.setUsername("maria");
        response.setEmail("maria@example.com");

        assertEquals(2, response.getId());
        assertEquals("maria", response.getUsername());
        assertEquals("maria@example.com", response.getEmail());
    }
}

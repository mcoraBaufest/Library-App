package com.libraryapp.dto.user.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRequestTest {

    @Test
    void shouldStoreUserRequestValues() {
        UserRequest request = new UserRequest();
        request.setUsername("juan");
        request.setEmail("juan@example.com");

        assertEquals("juan", request.getUsername());
        assertEquals("juan@example.com", request.getEmail());
    }
}

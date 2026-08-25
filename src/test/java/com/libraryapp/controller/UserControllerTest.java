package com.libraryapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.dto.user.request.UserRequest;
import com.libraryapp.dto.user.response.UserResponse;
import com.libraryapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Carga solo la capa MVC y el controlador de usuarios.
@WebMvcTest(UserController.class)
// Desactiva los filtros para enfocar las pruebas en el controller.
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // Verifica que POST /users cree un usuario y responda 201.
    @Test
    void shouldAddUser() throws Exception {
        when(userService.addUser(any(UserRequest.class)))
                .thenReturn(new UserResponse(1, "juan", "juan@example.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("juan", "juan@example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("juan"));
    }

    // Verifica que se rechacen usuarios con datos inválidos.
    @Test
    void shouldRejectInvalidUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("", "bad-email"))))
                .andExpect(status().isBadRequest());
    }

    // Verifica que GET /users devuelva todos los usuarios.
    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(
                new UserResponse(1, "juan", "juan@example.com")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("juan"));
    }

    // Verifica que GET /users/{id} devuelva un usuario existente.
    @Test
    void shouldGetUser() throws Exception {
        when(userService.getUser(1))
                .thenReturn(Optional.of(new UserResponse(1, "juan", "juan@example.com")));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    // Verifica que GET /users/{id} devuelva 404 si no existe.
    @Test
    void shouldReturnNotFoundForMissingUser() throws Exception {
        when(userService.getUser(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    // Verifica que PUT /users/{id} actualice un usuario.
    @Test
    void shouldUpdateUser() throws Exception {
        when(userService.updateUser(eq(1), any(UserRequest.class)))
                .thenReturn(Optional.of(new UserResponse(1, "maria", "maria@example.com")));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("maria", "maria@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maria"));
    }

    // Verifica que DELETE /users/{id} elimine un usuario existente.
    @Test
    void shouldDeleteUser() throws Exception {
        when(userService.removeUser(1)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    // Verifica que DELETE /users/{id} devuelva 404 si no existe.
    @Test
    void shouldReturnNotFoundWhenDeletingMissingUser() throws Exception {
        when(userService.removeUser(1)).thenReturn(false);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }

    private UserRequest request(String username, String email) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setEmail(email);
        return request;
    }
}

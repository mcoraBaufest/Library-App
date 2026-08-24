package com.libraryapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldRequireAuthenticationForApiEndpoints() throws Exception {
		mockMvc.perform(get("/books"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/authors"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldAllowAuthenticatedRequestsForAllApiEndpoints() throws Exception {
		mockMvc.perform(get("/authors")
						.header("Authorization", "Basic YWRtaW46MTIzNA=="))
				.andExpect(status().isOk());
	}
}

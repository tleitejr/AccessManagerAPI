package com.supera.accessmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supera.accessmanager.dto.auth.LoginRequest;
import com.supera.accessmanager.dto.auth.LoginResponse;
import com.supera.accessmanager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {

        LoginRequest req = new LoginRequest("email@teste.com", "123456");

        LoginResponse resp = new LoginResponse(
                "token123",
                "Bearer",
                3600L
        );

        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);

        when(authService.autenticar(captor.capture()))
                .thenReturn(resp);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        LoginRequest enviado = captor.getValue();

        assertThat(enviado.email()).isEqualTo("email@teste.com");
        assertThat(enviado.senha()).isEqualTo("123456");

        verify(authService).autenticar(enviado);
    }
}

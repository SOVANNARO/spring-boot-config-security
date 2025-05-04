package org.tutorials.springbootconfigsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tutorials.springbootconfigsecurity.dto.AuthenticationRequest;
import org.tutorials.springbootconfigsecurity.dto.AuthenticationResponse;
import org.tutorials.springbootconfigsecurity.dto.RegisterRequest;
import org.tutorials.springbootconfigsecurity.service.AuthenticationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John", "Doe", "john@example.com", "password");
        authenticationRequest = new AuthenticationRequest("john@example.com", "password");
        authenticationResponse = new AuthenticationResponse("accessToken", "refreshToken");
    }

    @Test
    void testRegister() throws Exception {
        when(authenticationService.register(any())).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void testAuthenticate() throws Exception {
        when(authenticationService.authenticate(any())).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }
}
package org.tutorials.springbootconfigsecurity.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.tutorials.springbootconfigsecurity.dto.AuthenticationRequest;
import org.tutorials.springbootconfigsecurity.dto.AuthenticationResponse;
import org.tutorials.springbootconfigsecurity.dto.RegisterRequest;
import org.tutorials.springbootconfigsecurity.entity.Role;
import org.tutorials.springbootconfigsecurity.entity.User;
import org.tutorials.springbootconfigsecurity.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John", "Doe", "john@example.com", "password");
        authenticationRequest = new AuthenticationRequest("john@example.com", "password");
        user = new User(1, "John", "Doe", "john@example.com", "encodedPassword", Role.USER);
    }

    @Test
    void testRegister() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateAccessToken(any())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any())).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void testAuthenticate() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any())).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }
}
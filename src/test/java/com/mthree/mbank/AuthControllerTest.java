package com.mthree.mbank;

import com.mthree.mbank.controller.ApiResponse;
import com.mthree.mbank.controller.AuthController;
import com.mthree.mbank.dto.auth.LoginRequest;
import com.mthree.mbank.dto.auth.LoginResponse;
import com.mthree.mbank.dto.auth.RegisterRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.security.JwtUtils;
import com.mthree.mbank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthController class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class) // Integrates Mockito with JUnit 5
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    private RegisterRequest registerRequest;
    private UserDTO userDTO;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setUp() {
        // Initialize RegisterRequest
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setPassword("P@ssw0rd!");
        registerRequest.setEmail("testuser@example.com");
        // Set other fields as necessary

        // Initialize UserDTO
        userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("testuser@example.com");
        // Set other fields as necessary

        // Initialize LoginRequest
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("P@ssw0rd!");
    }

    /**
     * Test the registerUser method of AuthController for successful registration.
     */
    @Test
    public void testRegisterUser_Success() {
        // Arrange
        when(userService.createUser(any(RegisterRequest.class))).thenReturn(userDTO);

        // Act
        ResponseEntity<?> response = authController.registerUser(registerRequest);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("User registered successfully!", apiResponse.getMessage());
        assertEquals(userDTO, apiResponse.getData());

        // Verify interactions
        verify(userService, times(1)).createUser(any(RegisterRequest.class));
    }

    /**
     * Test the registerUser method of AuthController when registration fails due to invalid data.
     */
    @Test
    public void testRegisterUser_InvalidData() {
        // Arrange
        when(userService.createUser(any(RegisterRequest.class))).thenThrow(new IllegalArgumentException("Invalid registration data"));

        // Act
        ResponseEntity<?> response = authController.registerUser(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Error: Invalid registration data", apiResponse.getMessage());
        assertEquals(null, apiResponse.getData());

        // Verify interactions
        verify(userService, times(1)).createUser(any(RegisterRequest.class));
    }

    /**
     * Test the authenticateUser method of AuthController for successful login.
     */
    @Test
    public void testAuthenticateUser_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken("testUser")).thenReturn("mocked-jwt-token");

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Login successful", apiResponse.getMessage());
        LoginResponse loginResponse = (LoginResponse) apiResponse.getData();
        assertEquals("mocked-jwt-token", loginResponse.getToken());

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken("testUser");
    }

    /**
     * Test the authenticateUser method of AuthController when login fails due to bad credentials.
     */
    @Test
    public void testAuthenticateUser_BadCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Error: Invalid username or password!", apiResponse.getMessage());
        assertEquals("Invalid credentials", apiResponse.getData());

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(0)).generateJwtToken(anyString());
    }

    /**
     * Test the authenticateUser method of AuthController when login fails due to username not found.
     */
    @Test
    public void testAuthenticateUser_UsernameNotFound() {
        // Arrange
        // Use Spring's UsernameNotFoundException which extends AuthenticationException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("Username not found"));

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Expected HTTP status UNAUTHORIZED");
        assertNotNull(response.getBody(), "Response body should not be null");

        // Cast the response body to ApiResponse
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("Error: Username not found!", apiResponse.getMessage(), "Unexpected error message");
        assertEquals("Username not found", apiResponse.getData(), "Unexpected error details");

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(0)).generateJwtToken(anyString()); // JWT should not be generated on failure
    }
}
package com.mthree.mbank.controller;

import com.mthree.mbank.dto.auth.LoginRequest;
import com.mthree.mbank.dto.auth.LoginResponse;
import com.mthree.mbank.dto.auth.RegisterRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.security.JwtUtils;
import com.mthree.mbank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related requests.
 * Provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for user authentication and registration")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * Endpoint for user registration.
     *
     * @param registerRequest the request containing user registration details
     * @return ResponseEntity with the status and message of the registration process
     */
    @Operation(summary = "Create new user")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Attempt to create a new user and retrieve the created UserDTO
            UserDTO createdUser = userService.createUser(registerRequest);
            // Return a response indicating successful registration
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User registered successfully!", createdUser));
        } catch (IllegalArgumentException e) {
            // Return a bad request response if user creation fails due to validation issues
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Endpoint for user login.
     *
     * @param loginRequest the request containing user login credentials
     * @return ResponseEntity with the status and JWT token if authentication is successful
     */
    @Operation(summary = "Log in")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user using provided credentials
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Generate JWT token for the authenticated user
            String jwt = jwtUtils.generateJwtToken(loginRequest.getUsername());

            // Return a response indicating successful login and include the JWT token
            return ResponseEntity.ok(new ApiResponse("Login successful", new LoginResponse(jwt)));
        } catch (BadCredentialsException e) {
            // Log the warning for bad credentials
            log.warn("Invalid login attempt for user: {}", loginRequest.getUsername());
            // Return an unauthorized response if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Error: Invalid username or password!", e.getMessage()));
        } catch (AuthenticationException e) {
            // Log for any other authentication exceptions
            log.warn("Authentication failed: {}", e.getMessage());
            // Return a response indicating that the username is not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Error: Username not found!", e.getMessage()));
        }
    }
}
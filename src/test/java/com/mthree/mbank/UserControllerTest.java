package com.mthree.mbank;

import com.mthree.mbank.controller.ApiResponse;
import com.mthree.mbank.controller.UserController;
import com.mthree.mbank.dto.user.UpdateUserRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.UserProfile;
import com.mthree.mbank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserController class.
 */
@ExtendWith(MockitoExtension.class) // Use Mockito Extension for JUnit 5
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        // Initialize mock user and DTO
        user = new User();
        UserProfile profile = new UserProfile(); // Initialize UserProfile
        user.setProfile(profile); // Associate the profile with the user
        user.getProfile().setUsername("testUser");
        user.getProfile().setEmail("test@example.com");

        userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("test@example.com");
    }

    @Test
    public void testGetProfile() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(userService.getUserDto(user)).thenReturn(userDTO);

        // Act
        ResponseEntity<ApiResponse> response = userController.getProfile(userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Profile retrieved successfully", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, times(1)).getUserDto(user);
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("newEmail@example.com");

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.updateUser(eq("testUser"), any(UpdateUserRequest.class))).thenReturn(userDTO);

        // Act
        ResponseEntity<ApiResponse> response = userController.updateUser(updateUserRequest, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User profile updated successfully", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(userService, times(1)).updateUser(eq("testUser"), any(UpdateUserRequest.class));
    }
}
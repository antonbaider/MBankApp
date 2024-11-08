package com.mthree.mbank;

import com.mthree.bankmthree.dto.user.UpdateUserRequest;
import com.mthree.bankmthree.dto.user.UserDTO;
import com.mthree.bankmthree.entity.User;
import com.mthree.bankmthree.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.getProfile().setUsername("testUser");
        user.getProfile().setEmail("test@example.com");
        userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("test@example.com");
    }

    @Test
    public void testGetProfile() {
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(userService.getUserDto(user)).thenReturn(userDTO);

        ResponseEntity<ApiResponse> response = userController.getProfile(userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Profile retrieved successfully", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());
    }

    @Test
    public void testUpdateUser() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("newEmail@example.com");

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.updateUser(eq("testUser"), any(UpdateUserRequest.class))).thenReturn(userDTO);

        ResponseEntity<ApiResponse> response = userController.updateUser(updateUserRequest, userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User profile updated successfully", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());
    }
}
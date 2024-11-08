package com.mthree.mbank.controller;

import com.mthree.mbank.dto.user.UpdateUserRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling user-related operations.
 * This includes retrieving and updating the user profile.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Operations related to users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to get the current user's profile.
     * Only accessible to authenticated users.
     *
     * @param userDetails the details of the authenticated user
     * @return ResponseEntity containing the UserDTO of the current user
     */
    @Operation(summary = "Get the current user's profile")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        UserDTO userDTO = userService.getUserDto(user);
        return ResponseEntity.ok(new ApiResponse("Profile retrieved successfully", userDTO));
    }

    /**
     * Endpoint to update the current user's profile.
     * Only accessible to authenticated users.
     *
     * @param updateUserRequest the request containing updated user details
     * @param userDetails       the details of the authenticated user
     * @return ResponseEntity containing the updated UserDTO
     */
    @Operation(summary = "Update the current user's profile")
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO updatedUser = userService.updateUser(userDetails.getUsername(), updateUserRequest);
        return ResponseEntity.ok(new ApiResponse("User profile updated successfully", updatedUser));
    }
}
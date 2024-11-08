package com.mthree.mbank.service;

import com.mthree.mbank.dto.auth.RegisterRequest;
import com.mthree.mbank.dto.user.UpdateUserRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * UserService interface defines operations related to user management
 * in the banking application. It provides methods for user registration,
 * updating user information, retrieving user profiles, and managing
 * family member associations. Method-level security is enforced using
 * annotations.
 */
public interface UserService {

    /**
     * Retrieves a UserDTO representation of the specified user.
     *
     * @param user the User entity to be converted to UserDTO
     * @return UserDTO representation of the user
     */
    @Transactional(readOnly = true)
    UserDTO getUserDto(User user);

    /**
     * Retrieves a User entity based on the registration request.
     *
     * @param registerRequest the registration request containing user details
     * @return the User entity corresponding to the registration request
     */
    User getUser(RegisterRequest registerRequest);

    /**
     * Creates a new user based on the provided registration details.
     *
     * @param registerRequest the registration request containing user details
     * @return UserDTO representation of the created user
     */
    @Transactional
    UserDTO createUser(@Valid RegisterRequest registerRequest);

    /**
     * Updates user information for a specified username.
     * Access is restricted to users with the 'ADMIN' role or the user
     * themselves.
     *
     * @param username           the username of the user to be updated
     * @param updateUserRequest  the request containing updated user details
     * @return UserDTO representation of the updated user
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    UserDTO updateUser(String username, UpdateUserRequest updateUserRequest);

    /**
     * Adds a family member to a user. Access is restricted to users with
     * the 'ADMIN' role or the user themselves.
     *
     * @param userId           the ID of the user to whom the family member will be added
     * @param familyMemberId   the ID of the family member to be added
     */
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    void addFamilyMember(Long userId, Long familyMemberId);

    /**
     * Retrieves all family members associated with a user. Access is restricted
     * to users with the 'ADMIN' role or the user themselves.
     *
     * @param userId the ID of the user whose family members are to be retrieved
     * @return a set of User entities representing the user's family members
     */
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    Set<User> getFamilyMembers(Long userId);

    /**
     * Finds a User entity by its username. Access is restricted to users
     * with the 'ADMIN' role or the user themselves.
     *
     * @param username the username of the user to be found
     * @return the User entity associated with the provided username
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    User findByUsername(String username);

    /**
     * Converts a set of User entities to a set of UserDTOs.
     *
     * @param users a set of User entities to be converted
     * @return a set of UserDTOs representing the users
     */
    Set<UserDTO> convertUsersToDTOs(Set<User> users);
}
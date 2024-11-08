package com.mthree.mbank.service.impl;

import com.mthree.mbank.constants.MessageConstants;
import com.mthree.mbank.dto.auth.RegisterRequest;
import com.mthree.mbank.dto.user.UpdateUserRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.UserProfile;
import com.mthree.mbank.entity.enums.CurrencyType;
import com.mthree.mbank.exception.user.*;
import com.mthree.mbank.mapper.UserMapper;
import com.mthree.mbank.repository.UserRepository;
import com.mthree.mbank.service.AccountService;
import com.mthree.mbank.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, AccountService accountService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    /**
     * Retrieves the UserDTO for a given user.
     *
     * @param user the user entity
     * @return the UserDTO representation of the user
     */
    @Transactional(readOnly = true)
    @CacheEvict(value = "users", key = "#user.username")
    @Override
    public UserDTO getUserDto(User user) {
        log.info(MessageConstants.Logs.FETCHING_USER_DTO, user.getUsername());
        return userMapper.toUserDTO(user);
    }

    /**
     * Converts a RegisterRequest DTO to a User entity.
     *
     * @param registerRequest the RegisterRequest DTO
     * @return the User entity
     */
    @Override
    public User getUser(RegisterRequest registerRequest) {
        return userMapper.toUser(registerRequest, passwordEncoder);
    }

    /**
     * Creates a new user based on the provided registration request.
     *
     * @param registerRequest the registration request containing user details
     * @return the created UserDTO
     */
    @Transactional
    @CacheEvict(value = "users", key = "#registerRequest.username")
    @Override
    public UserDTO createUser(@Valid RegisterRequest registerRequest) {
        log.info(MessageConstants.Logs.CREATING_USER, registerRequest.getUsername());

        // Validate the registration request fields
        validateUserFields(registerRequest);
        validatePassword(registerRequest.getPassword());

        // Map RegisterRequest to UserProfile using the mapper
        UserProfile profile = userMapper.toUserProfile(registerRequest, passwordEncoder);

        // Map the RegisterRequest to User entity using UserMapper
        User user = userMapper.toUser(registerRequest, passwordEncoder);

        // Use a try-catch block to manage potential exceptions during account creation
        try {
            // First, save the user to ensure the user ID is generated
            User savedUser = userRepository.save(user);

            // Create and associate accounts with the saved user
            Account usdAccount = accountService.createAndInitializeAccount(CurrencyType.USD, savedUser);
            Account eurAccount = accountService.createAndInitializeAccount(CurrencyType.EUR, savedUser);

            savedUser.setAccounts(new HashSet<>(Arrays.asList(usdAccount, eurAccount)));

            log.info(MessageConstants.Logs.USER_CREATED_SUCCESSFULLY, savedUser.getUsername());
            return userMapper.toUserDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            log.error(MessageConstants.Logs.FAILED_TO_SAVE_USER, e);
            handleDataIntegrityViolation(e);
        } catch (Exception e) {
            log.error(MessageConstants.Logs.FAILED_TO_SAVE_USER, e);
            throw new RuntimeException(MessageConstants.Exceptions.GENERAL_ERROR, e);
        }

        // Should never reach here due to exception handling
        throw new IllegalStateException(MessageConstants.Logs.FAILED_TO_SAVE_USER);
    }

    /**
     * Handles data integrity violations specifically to determine which constraint was violated.
     *
     * @param e the DataIntegrityViolationException
     */
    private void handleDataIntegrityViolation(DataIntegrityViolationException e) {
        // Inspect the root cause to determine which constraint was violated
        Throwable rootCause = e.getRootCause();
        if (rootCause instanceof ConstraintViolationException constraintViolationException) {
            String constraintName = constraintViolationException.getConstraintName();

            if ("unique_username".equalsIgnoreCase(constraintName)) {
                throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_ALREADY_EXISTS);
            } else if ("unique_email".equalsIgnoreCase(constraintName)) {
                throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_EMAIL_EXISTS);
            } else if ("unique_ssn".equalsIgnoreCase(constraintName)) {
                throw new UserSsnAlreadyExistsException(MessageConstants.Exceptions.USER_SSN_EXISTS);
            } else if ("unique_phone".equalsIgnoreCase(constraintName)) {
                throw new UserPhoneAlreadyExistsException(MessageConstants.Exceptions.USER_PHONE_EXISTS);
            }
        }
        // Fallback for unknown constraints
        throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_PROVIDED_DETAILS_EXIST);
    }

    /**
     * Updates an existing user's details.
     *
     * @param username          the username of the user to update
     * @param updateUserRequest the request containing updated user details
     * @return the updated UserDTO
     */
    @Transactional
    @CacheEvict(value = "users", key = "#username")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @Override
    public UserDTO updateUser(String username, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findByProfile_Username(username).orElseThrow(() -> new UserNotFoundException(MessageConstants.Exceptions.USER_NOT_FOUND));

        validateAndUpdateUserFields(updateUserRequest, user);

        try {
            User updatedUser = userRepository.save(user);
            log.info(MessageConstants.Logs.USER_UPDATED_SUCCESSFULLY, username);
            return getUserDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            log.error(MessageConstants.Logs.FAILED_TO_SAVE_USER, e);
            // Inspect the root cause to determine which constraint was violated
            Throwable rootCause = e.getRootCause();
            if (rootCause instanceof ConstraintViolationException constraintViolationException) {
                String constraintName = constraintViolationException.getConstraintName();

                if ("unique_email".equalsIgnoreCase(constraintName)) {
                    throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_EMAIL_EXISTS);
                } else if ("unique_phone".equalsIgnoreCase(constraintName)) {
                    throw new UserPhoneAlreadyExistsException(MessageConstants.Exceptions.USER_PHONE_EXISTS);
                }
            }
            // Fallback for unknown constraints
            throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_PROVIDED_DETAILS_EXIST);
        } catch (Exception e) {
            log.error(MessageConstants.Logs.FAILED_TO_SAVE_USER, e);
            throw new RuntimeException(MessageConstants.Exceptions.GENERAL_ERROR, e);
        }
    }

    /**
     * Validates and updates user fields based on the update request.
     *
     * @param updateUserRequest the update request containing new user details
     * @param user              the existing user entity to update
     */
    private void validateAndUpdateUserFields(UpdateUserRequest updateUserRequest, User user) {
        if (updateUserRequest.getFirstName() != null) {
            user.getProfile().setFirstName(updateUserRequest.getFirstName());
        }
        if (updateUserRequest.getLastName() != null) {
            user.getProfile().setLastName(updateUserRequest.getLastName());
        }

        if (updateUserRequest.getEmail() != null && !user.getProfile().getEmail().equals(updateUserRequest.getEmail())) {
            if (userRepository.existsByProfile_Email(updateUserRequest.getEmail())) {
                throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_EMAIL_EXISTS);
            }
            user.getProfile().setEmail(updateUserRequest.getEmail());
        }

        if (updateUserRequest.getPhone() != null && !user.getProfile().getPhone().equals(updateUserRequest.getPhone())) {
            if (userRepository.existsByProfile_Phone(updateUserRequest.getPhone())) {
                throw new UserPhoneAlreadyExistsException(MessageConstants.Exceptions.USER_PHONE_EXISTS);
            }
            user.getProfile().setPhone(updateUserRequest.getPhone());
        }

        if (updateUserRequest.getPassword() != null) {
            validatePassword(updateUserRequest.getPassword());
            user.getProfile().setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }
    }

    /**
     * Validates user fields during registration to ensure uniqueness.
     *
     * @param registerRequest the registration request containing user details
     */
    private void validateUserFields(@Valid RegisterRequest registerRequest) {
        if (userRepository.existsByProfile_Username(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByProfile_Email(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException(MessageConstants.Exceptions.USER_EMAIL_EXISTS);
        }
        if (userRepository.existsByProfile_Ssn(registerRequest.getSsn())) {
            throw new UserSsnAlreadyExistsException(MessageConstants.Exceptions.USER_SSN_EXISTS);
        }
        if (userRepository.existsByProfile_Phone(registerRequest.getPhone())) {
            throw new UserPhoneAlreadyExistsException(MessageConstants.Exceptions.USER_PHONE_EXISTS);
        }
    }

    /**
     * Validates the password against defined complexity rules.
     *
     * @param password the password to validate
     */
    private void validatePassword(String password) {
        // Example password complexity: at least 8 characters, includes letters, numbers, and special characters
        if (password.length() < 8 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()].*")) {
            throw new InvalidPasswordException(MessageConstants.Exceptions.USER_PASSWORD_RESTRICTION);
        }
    }

    /**
     * Adds a family member to a user's family set.
     *
     * @param userId         the ID of the user
     * @param familyMemberId the ID of the family member to add
     */
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Override
    public void addFamilyMember(Long userId, Long familyMemberId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(MessageConstants.Exceptions.USER_NOT_FOUND));
        User familyMember = userRepository.findById(familyMemberId).orElseThrow(() -> new UserNotFoundException(MessageConstants.Exceptions.FAMILY_MEMBER_NOT_FOUND));

        user.getFamily().add(familyMember);
        userRepository.save(user);
        log.info(MessageConstants.Logs.ADDING_FAMILY_MEMBER, familyMemberId, userId);
    }

    /**
     * Retrieves the family members of a user.
     *
     * @param userId the ID of the user
     * @return a set of family members
     */
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Override
    public Set<User> getFamilyMembers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(MessageConstants.Exceptions.USER_NOT_FOUND));
        log.info(MessageConstants.Logs.FETCHING_FAMILY_MEMBERS, userId);
        return user.getFamily();
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return the found User entity
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @Override
    public User findByUsername(String username) {
        log.info(MessageConstants.Logs.FINDING_USER_BY_USERNAME, username);
        return userRepository.findByProfile_Username(username).orElseThrow(() -> new UserNotFoundException(MessageConstants.Exceptions.USER_NOT_FOUND));
    }

    /**
     * Converts a set of User entities to a set of UserDTOs.
     *
     * @param users the set of User entities
     * @return the set of UserDTOs
     */
    @Override
    public Set<UserDTO> convertUsersToDTOs(Set<User> users) {
        log.info(MessageConstants.Logs.CONVERTING_USERS_TO_DTO);
        return users.stream().map(userMapper::toUserDTO).collect(Collectors.toSet());
    }
}
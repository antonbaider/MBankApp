package com.mthree.mbank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;


@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile implements Serializable {
    /**
     * Primary key for the User entity.
     * Automatically generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's unique username.
     * Must be unique and cannot be null or blank.
     */
    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * User's password.
     * Cannot be null or blank.
     * Note: Ensure that passwords are securely hashed before persisting.
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    /**
     * User's first name.
     * Cannot be null or blank.
     */
    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;
    /**
     * User's last name.
     * Cannot be null or blank.
     */
    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;
    /**
     * User's unique email address.
     * Must be a valid email format, unique, and cannot be null or blank.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;
    /**
     * User's phone number.
     * Must consist of 10 to 15 digits.
     * Must be unique if provided.
     */
    @Column(unique = true)
    @NotBlank(message = "Phone number is required")
    private String phone;

    /**
     * User's Social Security Number (SSN).
     * Must be unique and cannot be null or blank.
     * Excluded from generated toString() and equals/hashCode() methods for security reasons.
     */
    @NotBlank(message = "SSN is required")
    @Column(name = "ssn", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String ssn;
}
package com.mthree.mbank.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters  without spaces")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters without spaces")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,20}$", message = "Username must be 3-20 characters long and can only contain letters, digits, dots, underscores, and hyphens")
    @Schema(description = "Username for login", example = "johndoe")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters")
    @Schema(description = "Password for login", example = "P@ssw0rd", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address of the user", example = "john.doe@login.jsp.com")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phone;

    @NotBlank(message = "SSN is required")
    @Pattern(regexp = "^(?!000|666|9\\d\\d)(\\d{3})(?!00)(\\d{2})(?!0000)(\\d{4})$", message = "Invalid SSN format")
    @Schema(description = "Social Security Number", example = "123456789", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String ssn;

    @Schema(description = "Role of the user", example = "ROLE_USER")
    private String role;
}
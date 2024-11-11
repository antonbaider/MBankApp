package com.mthree.mbank.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the user", example = "john.doe@login.jsp.com")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phone;

    @Size(min = 5, max = 50, message = "Password must be between 8 and 50 characters")
    @Schema(description = "Password for login", example = "P@ssw0rd", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
}
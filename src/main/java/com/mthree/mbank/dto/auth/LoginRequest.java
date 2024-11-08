package com.mthree.mbank.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the user", example = "johndoe")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Username of the user", example = "P@ssw0rd")
    private String password;
}
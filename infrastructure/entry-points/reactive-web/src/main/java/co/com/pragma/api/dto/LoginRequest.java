package co.com.pragma.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (
        @Email
        @NotBlank(message = "The email is required")
        @Size(min = 10, max = 30, message = "The email should have between 4 and 30 characters")
        String email,

        @NotBlank(message = "The password is required")
        String password
) {}

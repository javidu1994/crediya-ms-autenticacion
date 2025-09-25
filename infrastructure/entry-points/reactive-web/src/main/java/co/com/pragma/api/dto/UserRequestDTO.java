package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be greater than or equal to 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "Base salary must be less than or equal to 15000000")
    private BigDecimal baseSalary;

    @NotNull(message = "Birthdate is required")
    private LocalDate birthdate;

    private String address;

    private String phoneNumber;
}

package co.com.pragma.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private BigDecimal baseSalary;
    private LocalDate birthdate;
    private String address;
    private String phone;
}

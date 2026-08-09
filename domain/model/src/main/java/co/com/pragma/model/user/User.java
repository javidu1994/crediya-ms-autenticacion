package co.com.pragma.model.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class User {

    private Long idUser;
    private String dni;
    private String name;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private BigDecimal baseSalary;
    private LocalDate birthdate;
    private String password;
    private Long idRol;
}

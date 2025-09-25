package co.com.pragma.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long idUser;
    private String name;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private BigDecimal baseSalary;
    private LocalDate birthdate;
}

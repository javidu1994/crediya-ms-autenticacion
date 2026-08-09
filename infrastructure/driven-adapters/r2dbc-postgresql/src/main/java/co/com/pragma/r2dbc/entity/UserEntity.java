package co.com.pragma.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column("id_user")
    private Long idUser;

    @Column("name")
    private String name;

    @Column("lastname")
    private String lastName;

    @Column("dni")
    private String dni;

    @Column("birthdate")
    private LocalDate birthdate;

    @Column("address")
    private String address;

    @Column("phone")
    private String phoneNumber;

    @Column("email")
    private String email;

    @Column("base_salary")
    private BigDecimal baseSalary;

    @Column("password")
    private String password;

    @Column("id_rol")
    private Long idRol;
}

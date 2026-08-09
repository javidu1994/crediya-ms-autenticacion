package co.com.pragma.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Role {

    private Long idRole;
    private String name;
    private String description;
}

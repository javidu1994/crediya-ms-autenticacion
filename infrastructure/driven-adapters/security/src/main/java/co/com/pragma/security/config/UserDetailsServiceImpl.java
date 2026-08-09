package co.com.pragma.security.config;

import co.com.pragma.usecase.role.RoleUseCase;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserUseCase userUseCase; // Inyectamos el puerto del dominio
    private final RoleUseCase roleUseCase;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        /*return userUseCase.findByEmail(username) // Buscamos en el dominio
                .map(SecurityUser::new) // Convertimos el User de dominio a SecurityUser
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));*/
        return userUseCase.findByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .flatMap(user -> roleUseCase.findById(user.getIdRol())
                        .map(rol -> {
                            // Convertimos el nombre del rol (ej: "ADMIN") en una autoridad de Spring
                            List<GrantedAuthority> authorities = List.of(
                                    new SimpleGrantedAuthority("ROLE_" + rol.getName().toUpperCase())
                            );

                            return new org.springframework.security.core.userdetails.User(
                                    user.getEmail(),
                                    user.getPassword(),
                                    authorities
                            );
                        }));
    }
}

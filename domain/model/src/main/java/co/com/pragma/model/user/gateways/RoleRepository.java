package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    Mono<Role> save(Role role);

    Flux<Role> findAll();

    Mono<Role> update(Role role);

    Mono<Role> findById(Long id);

    Mono<Void> deleteById(Long roleId);

    Mono<Boolean> existRoleByName(String name);

    Mono<Role> findByName(String name);

}

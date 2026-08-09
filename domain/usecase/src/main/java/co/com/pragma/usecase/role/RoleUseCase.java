package co.com.pragma.usecase.role;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class RoleUseCase {

    private static final Logger LOGGER = Logger.getLogger(RoleUseCase.class.getName());
    private final RoleRepository roleRepository;

    public Mono<Role> save(Role role) {
        return roleRepository.existRoleByName(role.getName())
                .doOnNext(u -> LOGGER.info("listenSaveRole with data: {} " + role.toString()))
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.error(new IllegalStateException("The name " + role.getName() + " is already registered")))
                .flatMap(exist -> roleRepository.save(role));
    }

    public Mono<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public Flux<Role> findAll() {
        return roleRepository.findAll();
    }

    public Mono<Role> update(Role role) {
        return roleRepository.save(role);
    }

    public Mono<Void> deleteById(Long userId) {
        return roleRepository.deleteById(userId);
    }

    public Mono<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public Mono<Boolean> existByName(String name) {
        return roleRepository.existRoleByName(name);
    }
}

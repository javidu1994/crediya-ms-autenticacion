package co.com.pragma.r2dbc;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, Long, RoleReactiveRepository>
        implements RoleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleRepositoryAdapter.class);

    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Role.class));
    }

    @Override
    public Mono<Role> save(Role role) {
        return super.save(role)
                .doOnSuccess(r -> LOGGER.debug("save role: {}", r));
    }

    @Override
    public Flux<Role> findAll() {
        return super.findAll()
                .doOnNext(r -> LOGGER.debug("roles returned: {}", r))
                .map(r -> r);
    }

    @Override
    public Mono<Role> update(Role role) {
        return super.save(role)
                .doOnSuccess(r -> LOGGER.debug("update role: {}", r));
    }

    @Override
    public Mono<Role> findById(Long id) {
        return super.findById(id)
                .doOnNext(r -> LOGGER.debug("findById with param: {}", id));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id)
                .doOnSuccess(r -> LOGGER.debug("delete role: {}", id));
    }

    @Override
    public Mono<Boolean> existRoleByName(String name) {
        Role role = Role.builder()
                .name(name)
                .build();
        return super.findByExample(role)
                .doOnNext(exists -> LOGGER.debug("existRoleByName with param: {} -> {}", name, exists))
                .next()
                .map(r -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Role> findByName(String name) {
        return super.repository.findByName(name)
                .map(super::toEntity)
                .doOnNext(r -> LOGGER.debug("findByName with param: {} -> {}", name, r));
    }

}

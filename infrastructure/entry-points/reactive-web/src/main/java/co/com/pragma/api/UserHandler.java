package co.com.pragma.api;

import co.com.pragma.api.dto.*;
import co.com.pragma.api.exception.ValidationException;
import co.com.pragma.model.user.User;
import co.com.pragma.security.jwt.JwtService;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);
    //private final TransactionalOperator transactionalOperator;
    private final UserUseCase userUseCase;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .doOnNext(u -> LOGGER.info("listenSaveUser with data: {} ", u.toString()))
                .flatMap(this::validateRequest)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return objectMapper.convertValue(user, User.class);
                })
                .doOnNext(u -> LOGGER.info("afterConvert with data: {} ", u.toString()))
                .flatMap(userUseCase::save)
                //.as(transactionalOperator::transactional)
                //.map(SecurityUser::new)
                .flatMap(saved -> ServerResponse.created(serverRequest.uriBuilder().path("/{idUsuario}")
                                .build(saved.getIdUser()))
                        .contentType(MediaType.APPLICATION_NDJSON)
                        .bodyValue(saved)
                );
    }

    private Mono<UserRequestDTO> validateRequest(UserRequestDTO requestDTO) {
        Errors errors = new BeanPropertyBindingResult(requestDTO, UserRequestDTO.class.getName());
        validator.validate(requestDTO, errors);

        if (errors.hasErrors()) {
            List<ValidationError> fieldErrors = errors.getFieldErrors()
                    .stream()
                    .map(err -> new ValidationError(err.getField(), err.getDefaultMessage()))
                    .collect(Collectors.toList());
            LOGGER.info("Errors while listenSaveUser: {}", fieldErrors);
            throw new ValidationException(fieldErrors);
        }

        return Mono.just(requestDTO);
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                //.contentType(MediaType.APPLICATION_NDJSON)
                //.contentType(MediaType.TEXT_EVENT_STREAM)
                .body(userUseCase.findAll(), UserDTO.class);
    }

    public Mono<ServerResponse> listenGetExistUserByDni(ServerRequest serverRequest) {
        String dni = serverRequest.queryParam("dni").orElse("");

        if (dni.isEmpty()) {
            return ServerResponse.badRequest()
                    .bodyValue("The parameter 'dni' is required");
        }
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(userUseCase.existByDni(dni), Boolean.class);
    }

    public Mono<ServerResponse> listenLoginUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
                .doOnNext(request -> LOGGER.info("login with data: {} ", request.toString()))
                .flatMap(request -> authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.email(), request.password())
                )
                .flatMap(auth -> {
                    String token = jwtService.generateToken((UserDetails) auth.getPrincipal());
                    return ServerResponse.ok().header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_NDJSON)
                            .bodyValue(LoginResponse.builder().token(token).build());
                })
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_NDJSON)
                                .bodyValue(e.getMessage())));
    }

    public Mono<ServerResponse> listenGetUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(userUseCase.findByEmail(email), UserDTO.class);
    }
}

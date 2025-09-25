package co.com.pragma.api;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.ValidationError;
import co.com.pragma.api.exception.ValidationException;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);
    private final TransactionalOperator transactionalOperator;
    private final UserUseCase userUseCase;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .doOnNext(u -> LOGGER.debug("listenSaveUser with data: {} ", u.toString()))
                .flatMap(this::validateRequest)
                .map(user -> objectMapper.convertValue(user, User.class))
                .flatMap(userUseCase::save)
                .as(transactionalOperator::transactional)
                .flatMap(saved -> ServerResponse.created(serverRequest.uriBuilder().path("/{idUsuario}").build(saved.getIdUser()))
                        .contentType(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
                //.contentType(MediaType.APPLICATION_NDJSON)
                //.contentType(MediaType.TEXT_EVENT_STREAM)
                .body(userUseCase.findAll(), UserDTO.class);
    }
}

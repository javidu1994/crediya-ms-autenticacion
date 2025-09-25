package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.openapi.UserOpenApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UserPath userPath;
    private final UserHandler userHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler handler) {
        return route()
                .POST(userPath.getUsers(), userHandler::listenSaveUser, UserOpenApi::saveUser)
                .GET(userPath.getUsers(), userHandler::listenGetAllUsers, UserOpenApi::getAllUsers)
                .build();
    }
}

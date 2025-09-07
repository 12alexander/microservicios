package co.com.bancolombia.api;

import co.com.bancolombia.api.auth.AuthHandler;
import co.com.bancolombia.api.constants.ApiPaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthRouterRest {
    
    @Bean
    public RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return route()
                .POST(ApiPaths.LOGIN, accept(MediaType.APPLICATION_JSON), authHandler::login)
                .GET("/api/v1/auth/validate", authHandler::validateToken)
                .build();
    }
}

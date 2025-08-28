package co.com.bancolombia.api;

import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.api.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "registrarUsuario",
                    operation = @Operation(
                            operationId = "registrarUsuario",
                            summary = "Registrar un usuario",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario registrado",
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
                            }
                    )
            ),
    })
    public RouterFunction<ServerResponse> usuarioRoutes(UserHandler userHandler) {
        return route()
                .POST("/api/v1/usuarios", userHandler::saveUser)
                .build();
    }
}

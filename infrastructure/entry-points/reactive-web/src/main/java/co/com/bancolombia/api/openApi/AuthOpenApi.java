package co.com.bancolombia.api.openApi;

import co.com.bancolombia.api.auth.AuthHandler;
import co.com.bancolombia.api.auth.dto.AuthRequestDTO;
import co.com.bancolombia.api.auth.dto.AuthResponseDTO;
import co.com.bancolombia.api.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Tag(name = "Authentication", description = "Authentication and JWT token management endpoints")
public class AuthOpenApi {

    private static final String API_V1_AUTH_LOGIN = "/api/v1/auth/login";
    private static final String API_V1_AUTH_VALIDATE = "/api/v1/auth/validate";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = API_V1_AUTH_LOGIN,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "loginUser",
                            summary = "User Authentication",
                            description = "Authenticate user with email and password to obtain JWT token",
                            tags = {"Authentication"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "User login credentials",
                                    content = @Content(schema = @Schema(implementation = AuthRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Login successful - JWT token generated",
                                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request - missing or malformed data",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Authentication failed - invalid credentials",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_AUTH_VALIDATE,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "validateToken",
                    operation = @Operation(
                            operationId = "validateJwtToken",
                            summary = "Validate JWT Token",
                            description = "Validate JWT token and return user information if valid",
                            tags = {"Authentication"},
                            security = @SecurityRequirement(name = "bearerAuth"),
                            parameters = @Parameter(
                                    name = "Authorization",
                                    description = "Bearer token in format: Bearer {jwt-token}",
                                    required = true,
                                    in = ParameterIn.HEADER,
                                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Token is valid",
                                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing or invalid Authorization header",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Token is invalid or expired",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutesDoc() {
        return RouterFunctions.route(
                RequestPredicates.GET("/__dummy_auth__"),
                req -> ServerResponse.ok().build()
        );
    }
}
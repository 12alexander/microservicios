package co.com.bancolombia.api.openApi;

import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.dto.ErrorResponseDTO;
import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.api.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class UserOpenApi {

    private static final String API_V1_USERS = "/api/v1/users";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = API_V1_USERS,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "saveUser",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Create a new user",
                            description = "Creates a new user in the system",
                            requestBody = @RequestBody(
                                       required = true,
                                    description = "User data to create",
                                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "User created successfully",
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                                    @ApiResponse(responseCode = "409", description = "User already exists",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USERS + "/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getUserById",
                    operation = @Operation(
                            operationId = "getUserById",
                            summary = "Get user by ID",
                            description = "Retrieves a user by their unique identifier",
                            parameters = @Parameter(name = "id", description = "User ID", required = true),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "User found",
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "User not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USERS,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getAllUsers",
                    operation = @Operation(
                            operationId = "getAllUsers",
                            summary = "Get all users",
                            description = "Retrieves all users from the system",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRoutesDoc() {
        return RouterFunctions.route(
                RequestPredicates.GET("/__dummy__"),
                req -> ServerResponse.ok().build()
        );
    }
}

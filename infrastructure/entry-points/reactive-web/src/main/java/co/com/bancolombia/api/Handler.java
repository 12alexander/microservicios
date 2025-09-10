package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Template handler class following Clean Architecture principles.
 * This class serves as a template for new handlers in the system.
 * 
 * Remove commented code and implement actual use cases when needed.
 * Follow the pattern established in UserHandler and AuthHandler.
 */
@Component
@RequiredArgsConstructor
public class Handler {
    // Template methods - implement when needed
    // private final UseCase useCase;

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("Template handler - implement your use case");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("Template handler - implement your use case");
    }
}

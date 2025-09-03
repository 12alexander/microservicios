package co.com.bancolombia.api.config;

import co.com.bancolombia.api.constants.ApiPaths;
import co.com.bancolombia.api.enums.RolEnum;
import co.com.bancolombia.api.jwt.JwtAuthenticationFilter;
import co.com.bancolombia.api.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class SecurityConfig {
    
    private final JwtProvider jwtProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(auth -> {
                    configurePublicEndpoints(auth);
                    configureUserEndpoints(auth);
                    configureRolEndpoints(auth);
                    configureOtherEndpoints(auth);
                })
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private void configurePublicEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec auth) {
        auth.pathMatchers(HttpMethod.POST, ApiPaths.LOGIN).permitAll();
        auth.pathMatchers(ApiPaths.HEALTH).permitAll();
        auth.pathMatchers(ApiPaths.ACTUATOR).permitAll();
        auth.pathMatchers(
                ApiPaths.SWAGGER_UI,
                ApiPaths.SWAGGER_UI_RESOURCES,
                ApiPaths.API_DOCS,
                ApiPaths.WEBJARS_SWAGGER
        ).permitAll();
    }

    private void configureUserEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec auth) {
        auth.pathMatchers(HttpMethod.POST, ApiPaths.USERS).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.PUT, ApiPaths.USERS).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.DELETE, ApiPaths.USERSBYID).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.GET, ApiPaths.USERSALL).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.GET, ApiPaths.USERSBYID).access(this::isAdminOrClientForSelf);
    }

    private void configureRolEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec auth) {
        auth.pathMatchers(HttpMethod.POST, ApiPaths.ROL).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.PUT, ApiPaths.ROL).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.DELETE, ApiPaths.ROLBYID).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.GET, ApiPaths.ROLLALL).access(this::isAdminOnly);
        auth.pathMatchers(HttpMethod.GET, ApiPaths.ROLBYID).access(this::isAdminOnly);
    }

    private void configureOtherEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec auth) {
        auth.anyExchange().authenticated();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    private Mono<AuthorizationDecision> isAdminOnly(Mono<Authentication> authenticationMono,
                                                    AuthorizationContext context) {
        return authenticationMono
                .doOnNext(auth -> log.debug("[DEBUG] Authentication object: {}", auth))
                .map(auth -> {
                    Object credentials = auth.getCredentials();
                    if (credentials == null) {
                        log.debug("[DEBUG] No credentials found, denying access");
                        return new AuthorizationDecision(false);
                    }
                    String token = (String) credentials;
                    UUID roleId = jwtProvider.extractRoleId(token);
                    boolean allowed = roleId.equals(RolEnum.ADMIN.getId());
                    return new AuthorizationDecision(allowed);
                })
                .defaultIfEmpty(new AuthorizationDecision(false))
                .doOnNext(decision -> log.debug("[DEBUG] AuthorizationDecision: {}", decision.isGranted()));
    }

    private Mono<AuthorizationDecision> isAdminOrClientForSelf(Mono<Authentication> authenticationMono,
                                                               AuthorizationContext context) {
        return authenticationMono
                .map(auth -> {
                    String token = (String) auth.getCredentials();
                    UUID roleId = jwtProvider.extractRoleId(token);
                    UUID userId = jwtProvider.extractUserId(token);

                    // Admin can access any user
                    if (roleId.equals(RolEnum.ADMIN.getId())) {
                        return new AuthorizationDecision(true);
                    }

                    // Client can only access their own data
                    Object pathUserIdObj = context.getVariables().get("id");
                    String pathUserId = pathUserIdObj != null ? pathUserIdObj.toString() : null;
                    if (pathUserId != null && roleId.equals(RolEnum.CLIENT.getId())) {
                        boolean allowed = userId.toString().equals(pathUserId);
                        return new AuthorizationDecision(allowed);
                    }

                    return new AuthorizationDecision(false);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
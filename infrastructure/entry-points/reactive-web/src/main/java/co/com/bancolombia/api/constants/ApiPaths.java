package co.com.bancolombia.api.constants;

public final class ApiPaths {

    // Auth endpoints
    public static final String LOGIN = "/api/v1/auth/login";
    
    // User endpoints
    public static final String USERS = "/api/v1/users";
    public static final String USERSBYID = "/api/v1/users/{id}";
    public static final String USERSALL = "/api/v1/users";
    
    // Role endpoints
    public static final String ROL = "/api/v1/roles";
    public static final String ROLBYID = "/api/v1/roles/{id}";
    public static final String ROLLALL = "/api/v1/roles";
    
    // Public endpoints
    public static final String HEALTH = "/health";
    public static final String ACTUATOR = "/actuator/**";
    
    // Swagger/OpenAPI endpoints
    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String SWAGGER_UI_RESOURCES = "/swagger-ui/**";
    public static final String API_DOCS = "/v3/api-docs/**";
    public static final String WEBJARS_SWAGGER = "/webjars/**";

    private ApiPaths() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
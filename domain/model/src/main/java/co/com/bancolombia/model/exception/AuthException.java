package co.com.bancolombia.model.exception;

public class AuthException extends RuntimeException {

    public static final String CODE = "AUTH_FAILED";

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return CODE;
    }
}

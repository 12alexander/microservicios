package co.com.bancolombia.model.exception;

public class InvalidDataException extends BusinessException {

    public static final String CODE = "DATA_INVALID";

    public InvalidDataException(String message) {
        super(CODE, message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(CODE, message, cause);
    }
}

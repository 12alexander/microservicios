package co.com.bancolombia.model.exception;

public class UserExistsException extends BusinessException {

    public static final String CODE = "USER_EXISTS";

    public UserExistsException(String emailAddress) {
        super(CODE, "Ya existe un usuario registrado con el correo electrónico: " + emailAddress);
    }
}

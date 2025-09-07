package co.com.bancolombia.model.auth;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Auth {
    private UUID idUser;
    private UUID idRole;
    private String token;
    private String nameUser;
}

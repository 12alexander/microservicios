package co.com.bancolombia.model.user;
import co.com.bancolombia.model.role.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String id;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String emailAddress;
    private BigDecimal baseSalary;
    private String idRol;
    private Role role;
    private String password;

    public static class UserBuilder {
        public UserBuilder name(String name) {
            if(name == null || name.trim().isEmpty()){
                throw new IllegalArgumentException("Nombres no puede ser Nulo o Vacio");
            }
            this.name = name.trim();
            return this;
        }

        public UserBuilder lastName(String lastName) {
            if(lastName == null || lastName.trim().isEmpty()){
                throw new IllegalArgumentException("Apellidos no puede ser Nulo o Vacio");
            }
            this.lastName = lastName.trim();
            return this;
        }

        public UserBuilder emailAddress(String emailAddress) {
            if(emailAddress == null || emailAddress.trim().isEmpty()){
                throw new IllegalArgumentException("Email no puede ser Nulo o Vacio");
            }
            String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
            if(!emailAddress.matches(emailRegex)){
                throw new IllegalArgumentException("Email no cumple con el formato ");
            }
            this.emailAddress = emailAddress;
            return this;
        }

        public UserBuilder baseSalary(BigDecimal baseSalary) {
            if(baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Salario no puede ser Nulo o menor que 0");
            }
            if(baseSalary.compareTo(new BigDecimal("15000000")) > 0){
                throw new IllegalArgumentException("Salario no puede ser mayor que 15000000");
            }
            this.baseSalary = baseSalary;
            return this;
        }

        public UserBuilder idRol(String idRol) {
            if(idRol == null || idRol.trim().isEmpty()){
                throw new IllegalArgumentException("ID del rol no puede ser Nulo o Vacio");
            }
            this.idRol = idRol.trim();
            return this;
        }

        public UserBuilder password(String password) {
            if(password == null || password.trim().isEmpty()){
                throw new IllegalArgumentException("Password no puede ser Nulo o Vacio");
            }
            if(password.length() < 3){
                throw new IllegalArgumentException("Password debe tener al menos 3 caracteres");
            }
            this.password = password;
            return this;
        }
    }

    public void  validateData(){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos no pueden ser nulos o vacíos");
        }

        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede ser nulo o vacío");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        if (!emailAddress.matches(emailRegex)) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }

        if (baseSalary == null) {
            throw new IllegalArgumentException("El salario base no puede ser nulo");
        }

        if (baseSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El salario base debe ser mayor a 0");
        }

        if (baseSalary.compareTo(new BigDecimal("15000000")) > 0) {
            throw new IllegalArgumentException("El salario base no puede ser mayor a 15,000,000");
        }

        if (idRol == null || idRol.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del rol no puede ser nulo o vacío");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("El password no puede ser nulo o vacío");
        }

        if (password.length() < 3) {
            throw new IllegalArgumentException("El password debe tener al menos 3 caracteres");
        }

    }

}

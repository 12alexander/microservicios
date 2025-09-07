package co.com.bancolombia.r2dbc.user.data;

import co.com.bancolombia.r2dbc.role.data.RoleData;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class UserData {
    @Id
    private String id;

    @Column("name")
    private String name;

    @Column("last_name")
    private String lastName;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("address")
    private String address;

    @Column("phone")
    private String phone;

    @Column("email_address")
    private String emailAddress;

    @Column("base_salary")
    private BigDecimal baseSalary;

    @Column("creation_date")
    private LocalDateTime creationDate;

    @Column("update_date")
    private LocalDateTime updateDate;

    @Column("id_rol")
    private String idRol;

    @Column("password")
    private String password;

    @Transient
    private RoleData role;

}

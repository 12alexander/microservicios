package co.com.bancolombia.r2dbc.user;

import co.com.bancolombia.r2dbc.user.data.UserData;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface UserR2dbcRepository extends ReactiveCrudRepository<UserData, String> {

    @Modifying
    @Query("INSERT INTO users (id, name, last_name, birth_date, address, phone, email_address, base_salary, id_rol, password, creation_date, update_date ) " +
            "VALUES (:id, :name, :last_name, :birth_date, :address, :phone, :email_address, :base_salary, :id_rol, :password, :creation_date, :update_date)")
    Mono<Integer> createUser(String id, String name, String last_name, LocalDate birth_date, String address, String phone, String email_address, BigDecimal base_salary, String id_rol, String password, LocalDateTime creation_date, LocalDateTime update_date);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email_address = :emailAddress)")
    Mono<Boolean> existsByEmailAddress(String emailAddress);

    Mono<UserData> findByEmailAddress(String emailAddress);

}

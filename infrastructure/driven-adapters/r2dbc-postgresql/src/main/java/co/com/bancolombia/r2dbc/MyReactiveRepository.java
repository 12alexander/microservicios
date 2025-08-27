package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.user.data.UserData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends R2dbcRepository<UserData, String> {

}

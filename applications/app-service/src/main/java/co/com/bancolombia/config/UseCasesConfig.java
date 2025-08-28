package co.com.bancolombia.config;

import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.user.UserUseCase;
import co.com.bancolombia.usecase.user.interfaces.IUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
/*@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)*/
public class UseCasesConfig {
       /* @Bean
        public IRegisterUserUseCase userUseCaseI(UserRepository repository, RoleRepository roleRepository) {
                return new RegisterUserUseCase( repository,roleRepository );
        }*/
/*
        @Bean
        public IUserUseCase usuarioUseCase(UserRepository userRepository, RoleRepository roleRepository) {
                return new UserUseCase(userRepository, roleRepository);
        }*/
       @Bean
       public IUserUseCase userUseCase(UserRepository userRepository, RoleRepository roleRepository) {
           return new UserUseCase(userRepository, roleRepository);
       }
}

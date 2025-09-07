package co.com.bancolombia.config;

import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.auth.AuthUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import co.com.bancolombia.usecase.user.interfaces.IUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class UseCasesConfig {
    
    @Bean
    @Primary
    public UserUseCase userUseCase(UserRepository userRepository, RoleRepository roleRepository) {
        return new UserUseCase(userRepository, roleRepository);
    }
    
    @Bean
    public AuthUseCase authUseCase(UserUseCase userUseCase) {
        return new AuthUseCase(userUseCase);
    }
}

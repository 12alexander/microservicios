package co.com.bancolombia.config;

import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.role.gateways.RoleRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(classes = UseCasesConfig.class)
class UseCasesConfigTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @Autowired
    private UserUseCase userUseCase;

    @Test
    void testContextLoads() {
        assertNotNull(userUseCase);
    }

    @Test
    void testUserUseCaseIsCreated() {
        assertNotNull(userUseCase);
    }
}
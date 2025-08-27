package co.com.bancolombia.config;

import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.role.gateways.RoleRepository;

@SpringBootTest
@ContextConfiguration(classes = {UseCasesConfig.class})
class UseCasesConfigTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Test
    void testContextLoads() {
        // Test que el contexto carga correctamente
    }
}
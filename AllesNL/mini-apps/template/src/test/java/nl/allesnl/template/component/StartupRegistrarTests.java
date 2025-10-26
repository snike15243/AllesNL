package nl.allesnl.template.component;

import nl.allesnl.template.contracts.template.RegistrationResponse;
import nl.allesnl.template.service.InternalClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Combined unit and integration tests for StartupRegistrar.
 */
class StartupRegistrarTests {

    // --- UNIT TEST SECTION ---

    @Nested
    @ExtendWith(MockitoExtension.class)
    class UnitTests {

        @Mock
        private InternalClientService internalClientService;

        @Mock
        private RegistrationState registrationState;

        private StartupRegistrar registrar;

        @BeforeEach
        void setUp() {
            registrar = new StartupRegistrar(internalClientService, registrationState);

            // Simulate Spring’s @Value injection
            ReflectionTestUtils.setField(registrar, "appName", "test-app" );
        }

        @Test
        void onApplicationReady_successfulRegistration_setsRegistrationId() {
            // given
            var response = new RegistrationResponse(42);
            when(internalClientService.register(eq("test-app"), eq("http://test-app:8080/internal")))
                    .thenReturn(response);

            // when
            registrar.onApplicationReady();

            // then
            verify(registrationState).setRegistrationId(42);
            verify(internalClientService).register("test-app", "http://test-app:8080/internal");
        }

        @Test
        void onApplicationReady_registrationFails_setsRegistrationIdToMinusOne() {
            // given
            when(internalClientService.register(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Connection failed"));

            // when
            registrar.onApplicationReady();

            // then
            verify(registrationState).setRegistrationId(-1);
        }
    }

    // --- INTEGRATION TEST SECTION ---
    @Nested
    @WebMvcTest(StartupRegistrar.class)
    class IntegrationTests {

    }
}

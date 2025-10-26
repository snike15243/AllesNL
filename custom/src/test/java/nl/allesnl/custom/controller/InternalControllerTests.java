package nl.allesnl.custom.controller;

import nl.allesnl.custom.component.RegistrationState;
import nl.allesnl.custom.contracts.miniapp.MiniAppDetails;
import nl.allesnl.custom.service.ExternalClientService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Combined unit and integration tests for InternalController.
 */
class InternalControllerTests {

    // --- UNIT TEST SECTION ---

    @Nested
    @ExtendWith(MockitoExtension.class)
    class UnitTests {

        @Mock
        private RegistrationState registrationState;

        @Mock
        private ExternalClientService externalClientService;

        private InternalController controller;

        @BeforeEach
        void setUp() {
            controller = new InternalController(registrationState, externalClientService);
            ReflectionTestUtils.setField(controller, "appName", "test-app");
            ReflectionTestUtils.setField(controller, "appDescription", "This is a test app");
            ReflectionTestUtils.setField(controller, "logoUrl", "https://cdn-icons-png.flaticon.com/512/1163/1163661.png");
        }

        @Test
        void getDetails_shouldReturnMiniAppDetails() {
            // Given
            long expectedRegistrationId = 1;
            when(registrationState.getRegistrationId()).thenReturn(expectedRegistrationId);

            // When
            MiniAppDetails result = controller.getDetails().getBody();

            // Then
            assertNotNull(result);
            assertEquals(expectedRegistrationId, result.registrationId());
            assertEquals("test-app", result.appName());
            assertEquals("This is a test app", result.appDescription());
            assertEquals("https://cdn-icons-png.flaticon.com/512/1163/1163661.png", result.logoUrl());

            verify(registrationState).getRegistrationId();
        }

        @Test
        void getHtml_shouldServeHtmlIndex() {
            // Given
            // Ensure the file exists in src/test/resources/static/index.html for this test
            controller = new InternalController(registrationState, externalClientService);

            // When
            ResponseEntity<Resource> response = controller.getHtml();

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
            assertInstanceOf(ClassPathResource.class, response.getBody());

            Resource resource = response.getBody();
            assertTrue(resource.exists(), "HTML resource should exist");
        }

    }

    // --- INTEGRATION TEST SECTION ---

    @Nested
    @WebMvcTest(InternalController.class)
    class IntegrationTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RegistrationState registrationState;

        @MockitoBean
        private ExternalClientService externalClientService;

        @Test
        void getDetails_shouldReturnDetailsAsJson() throws Exception {
            // Given
            long registrationId = 2;
            when(registrationState.getRegistrationId()).thenReturn(registrationId);

            // When & Then
            mockMvc.perform(get("/internal/details"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.registrationId").value(registrationId))
                    .andExpect(jsonPath("$.appName").isString())
                    .andExpect(jsonPath("$.appDescription").isString())
                    .andExpect(jsonPath("$.logoUrl").isString());
        }

    }
}

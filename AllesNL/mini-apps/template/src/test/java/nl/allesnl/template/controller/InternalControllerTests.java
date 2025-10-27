package nl.allesnl.template.controller;

import nl.allesnl.template.component.InternalAuthInterceptor;
import nl.allesnl.template.component.RegistrationState;
import nl.allesnl.template.contracts.miniapp.MiniAppDetails;
import nl.allesnl.template.record.Quote;
import nl.allesnl.template.service.ExternalClientService;
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
import java.util.Map;
import org.springframework.http.HttpHeaders;


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
        void getQuote_shouldReturnQuote() {
            // Given
            Quote expectedQuote = new Quote(1, "Be yourself", "Oscar Wilde");
            when(externalClientService.getQuote()).thenReturn(expectedQuote);

            // When
            Quote actual = controller.getQuote().getBody();

            // Then
            assertNotNull(actual);
            assertEquals(expectedQuote, actual);
            verify(externalClientService).getQuote();
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

        @Test
        void postClientData_withUserHeader_buildsResponseCorrectly() {
            // Given
            Quote backendQuote = new Quote(10, "Stay positive", "Unknown");
            when(externalClientService.getQuote()).thenReturn(backendQuote);
            Map<String, Object> user = Map.of("name", "Alice");
            Map<String, Object> payload = Map.of("user", user);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Data-Type", "user");

            // When
            ResponseEntity<Map<String, Object>> response = controller.postClientData(payload, headers);

            // Then
            Map<String, Object> body = response.getBody();
            assertNotNull(body);
            assertTrue(body.containsKey("author"));
            assertTrue(body.containsKey("quote"));

            Quote authorQuote = (Quote) body.get("author");
            Quote personalized = (Quote) body.get("quote");

            assertEquals(backendQuote, authorQuote);
            assertEquals("Stay positive", personalized.quote());
            assertEquals("Alice", personalized.author());
        }

        @Test
        void getClientData_withQuoteHeader_returnsQuote() {
            Quote backendQuote = new Quote(5, "Be yourself", "Oscar Wilde");
            when(externalClientService.getQuote()).thenReturn(backendQuote);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Data-Type", "quote");

            ResponseEntity<Map<String, Object>> response = controller.getClientData(headers);

            Map<String, Object> body = response.getBody();
            assertEquals(backendQuote, body.get("quote"));
        }

        @Test
        void deleteClientData_alwaysReturnsMessageDelete() {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Data-Type", "whatever");

            ResponseEntity<Map<String, Object>> response = controller.deleteClientData(headers);

            Map<String, Object> body = response.getBody();
            assertEquals("Delete", body.get("Message"));
        }

        @Test
        void postClientData_withUnknownHeader_returnsUserError() {
            // Given
            HttpHeaders headers = new HttpHeaders();
            headers.add("Data-Type", "unknown"); // not "user"
            Map<String, Object> payload = Map.of("user", Map.of("name", "Alice"));

            // When
            ResponseEntity<Map<String, Object>> response = controller.postClientData(payload, headers);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Map<String, Object> body = response.getBody();
            assertNotNull(body);
            assertTrue(body.containsKey("error"));
            assertEquals("User error", body.get("error"));
            verifyNoInteractions(externalClientService);
        }

        @Test
        void getClientData_withUnknownHeader_returnsError() {
            // Given
            HttpHeaders headers = new HttpHeaders();
            headers.add("Data-Type", "something-else");

            // When
            ResponseEntity<Map<String, Object>> response = controller.getClientData(headers);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Map<String, Object> body = response.getBody();
            assertNotNull(body);
            assertTrue(body.containsKey("error"));
            assertEquals("Unknown Data-Type", body.get("error"));
            verifyNoInteractions(externalClientService);
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

        @MockitoBean
        private InternalAuthInterceptor internalAuthInterceptor;

        @Test
        void getDetails_shouldReturnDetailsAsJson() throws Exception {
            // Given
            long registrationId = 2;
            when(registrationState.getRegistrationId()).thenReturn(registrationId);
            when(internalAuthInterceptor.preHandle(any(), any(), any())).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/internal/details"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.registrationId").value(registrationId))
                    .andExpect(jsonPath("$.appName").isString())
                    .andExpect(jsonPath("$.appDescription").isString())
                    .andExpect(jsonPath("$.logoUrl").isString());
        }

        @Test
        void getQuote_shouldReturnQuoteAsJson() throws Exception {
            Quote expectedQuote = new Quote(1, "Be yourself", "Oscar Wilde");
            when(externalClientService.getQuote()).thenReturn(expectedQuote);
            when(internalAuthInterceptor.preHandle(any(), any(), any())).thenReturn(true);

            mockMvc.perform(get("/internal/quote"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(expectedQuote.id()))
                    .andExpect(jsonPath("$.quote").value(expectedQuote.quote()))
                    .andExpect(jsonPath("$.author").value(expectedQuote.author()));
        }
    }
}

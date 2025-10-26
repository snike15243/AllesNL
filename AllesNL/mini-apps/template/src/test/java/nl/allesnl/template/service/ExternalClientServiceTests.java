package nl.allesnl.template.service;


import nl.allesnl.template.record.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ExternalClientServiceTests {


    // --- UNIT TEST SECTION ---

    @Nested
    @ExtendWith(MockitoExtension.class)
    class UnitTests {

        @Mock
        private RestClient restClient;

        @Mock
        private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

        @Mock
        private RestClient.RequestHeadersSpec requestHeadersSpec;

        @Mock RestClient.ResponseSpec responseSpec;

        private ExternalClientService service;

        @BeforeEach
        void setup() {
            service = new ExternalClientService(restClient);
        }


        @Test
        void getQuote_shouldReturnQuoteObject() {
            // Given
            Quote expectedQuote = new Quote(12, "Be yourself; everyone else is already taken.", "Oscar Wilde");

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri("/quotes/random")).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(Quote.class)).thenReturn(expectedQuote);

            // When
            Quote actual = service.getQuote();

            // Then
            assertNotNull(actual);
            assertEquals(expectedQuote, actual);
            verify(restClient).get();
            verify(requestHeadersUriSpec).uri("/quotes/random");
            verify(requestHeadersSpec).retrieve();
            verify(responseSpec).body(Quote.class);
        }
    }

    // --- INTEGRATION TEST SECTION ---

    @Nested
    @RestClientTest(ExternalClientService.class)
    class IntegrationTests {

        @Autowired
        private ExternalClientService service;

        @Autowired
        private MockRestServiceServer server;

        @TestConfiguration
        static class TestConfig {
            @Bean
            @Qualifier("externalRestClient")
            RestClient externalRestClient(RestClient.Builder builder) {
                return builder.baseUrl("http://quoteslate:3000/api").build();
            }
        }

        @Test
        void getQuote_shouldFetchQuoteFromRemoteService() {
            // Given
            String jsonResponse = """
                {
                    "id": 42,
                    "quote": "Do or do not. There is no try.",
                    "author": "Yoda"
                }
                """;

            server.expect(requestTo("http://quoteslate:3000/api/quotes/random"))
                    .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

            // When
            Quote result = service.getQuote();

            // Then
            assertNotNull(result);
            assertEquals(42, result.id());
            assertEquals("Do or do not. There is no try.", result.quote());
            assertEquals("Yoda", result.author());
        }
    }
}

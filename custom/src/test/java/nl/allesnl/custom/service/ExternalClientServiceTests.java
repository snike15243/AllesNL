package nl.allesnl.custom.service;

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
    }
}

package nl.allesnl.template.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class RestClientConfiguration {

    @Bean
    @Qualifier("externalRestClient")
    public RestClient externalRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://quoteslate:3000/api")
                .build();
    }

    @Bean
    @Qualifier("internalRestClient")
    public RestClient internalRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://mini-app-service:8080/miniapp")
                .build();
    }

}

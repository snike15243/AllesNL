package nl.allesnl.template.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
class InternalAuthConfiguration {

    @Bean
    public String internalAuthToken() {
        return UUID.randomUUID().toString();
    }
}

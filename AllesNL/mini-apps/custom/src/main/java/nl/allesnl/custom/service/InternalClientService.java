package nl.allesnl.custom.service;

import nl.allesnl.custom.contracts.template.RegistrationRequest;
import nl.allesnl.custom.contracts.template.RegistrationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class InternalClientService {

    private final RestClient restClient;

    public InternalClientService(@Qualifier("internalRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public RegistrationResponse register(String appName, String baseUrl){
        RegistrationRequest request = new RegistrationRequest(appName, baseUrl);
        return restClient.post()
                .uri("/register")
                .body(request)
                .retrieve()
                .body(RegistrationResponse.class);
    }
}

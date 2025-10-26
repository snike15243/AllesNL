package nl.allesnl.template.service;


import nl.allesnl.template.record.Quote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalClientService {

    private final RestClient restClient;

    public ExternalClientService(@Qualifier("externalRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Quote getQuote() {
        return restClient.get()
                .uri("/quotes/random")
                .retrieve()
                .body(Quote.class);
    }

}

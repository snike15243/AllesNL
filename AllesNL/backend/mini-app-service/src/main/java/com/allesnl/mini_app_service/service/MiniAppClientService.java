package com.allesnl.mini_app_service.service;

import com.allesnl.mini_app_service.contracts.miniapp.MiniAppDetails;
import com.allesnl.mini_app_service.contracts.miniapp.MiniAppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import com.allesnl.mini_app_service.contracts.user.User;

import java.util.*;

@Service
@Slf4j
public class MiniAppClientService {

    private final RestClient.Builder restClientBuilder;
    private final MiniAppRegistryService miniAppRegistry;

    public MiniAppClientService(RestClient.Builder restClientBuilder, MiniAppRegistryService miniAppRegistry) {
        this.restClientBuilder = restClientBuilder;
        this.miniAppRegistry = miniAppRegistry;
    }

    public Optional<MiniAppDetails> getMiniAppDetails(Long miniAppId) {
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(miniAppId);
        if (optionalMiniAppInfo.isEmpty()) {
            return Optional.empty();
        }
        MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
        RestClient restClient = createRestClient(miniAppInfo.getBaseUrl());
        try {
            MiniAppDetails miniAppDetails = restClient.get()
                    .uri("/details")
                    .retrieve()
                    .body(MiniAppDetails.class);
            return Optional.ofNullable(miniAppDetails);
        } catch (RestClientResponseException e) {
            log.warn("Mini-app {} returned non-200 status {}: {}",
                    miniAppId, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.warn("Failed to reach mini-app {} at {}: {}",
                    miniAppId, miniAppInfo.getBaseUrl(), e.getMessage());
        }
        return Optional.empty();
    }

    public ResponseEntity<byte[]> getMiniAppHtml(Long miniAppId) {
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(miniAppId);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
        RestClient restClient = createRestClient(miniAppInfo.getBaseUrl());
        try {
            ResponseEntity<byte[]> response = restClient.get()
                    .uri("/html")
                    .retrieve()
                    .toEntity(byte[].class);

            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.TEXT_HTML)
                    .body(response.getBody());
        } catch (RestClientException e) {
            log.error("Failed to fetch HTML from mini-app {}: {}", miniAppId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    public ResponseEntity<byte[]> getMiniAppHtml(Long miniAppId, User user) {
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(miniAppId);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
        RestClient restClient = createRestClient(miniAppInfo.getBaseUrl());
        try {
            ResponseEntity<byte[]> response = restClient.post()
                    .uri("/html")
                    .body(user)
                    .retrieve()
                    .toEntity(byte[].class);

            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.TEXT_HTML)
                    .body(response.getBody());
        } catch (RestClientException e) {
            log.error("Failed to fetch HTML from mini-app {}: {}", miniAppId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    public ResponseEntity<Map<String, Object>> postClientData(Long miniAppId, Map<String, Object> incoming, String dataTypeHeader) {
        // Assuming you have some way to get miniAppInfo (not optionalMiniAppInfo here)
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(miniAppId);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
        RestClient restClient = createRestClient(miniAppInfo.getBaseUrl());

        try {
            ResponseEntity<Map<String, Object>> response = restClient.post()
                    .uri("/client")
                    .headers(outgoingHeaders -> {
                        if (dataTypeHeader != null) {
                            outgoingHeaders.add("Data-Type", dataTypeHeader);
                            System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
                        }
                    })
                    .body(incoming)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (RestClientException e) {
            log.error("Failed to fetch data from mini-app {}: {}", miniAppId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }



    public ResponseEntity<Map<String, Object>> getClientData(Long miniAppId, String dataTypeHeader) {
        // Assuming you have some way to get miniAppInfo (not optionalMiniAppInfo here)
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(miniAppId);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
        RestClient restClient = createRestClient(miniAppInfo.getBaseUrl());

        try {
            ResponseEntity<Map<String, Object>> response = restClient.get()
                    .uri("/client")
                    .headers(outgoingHeaders -> {
                        if (dataTypeHeader != null) {
                            outgoingHeaders.add("Data-Type", dataTypeHeader);
                            System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
                        }
                    })
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (RestClientException e) {
            log.error("Failed to fetch data from mini-app {}: {}", miniAppId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    public ResponseEntity<Map<String, Object>> deleteClientData(Long miniAppId, String dataTypeHeader) {
        // Assuming you have some way to get miniAppInfo (not optionalMiniAppInfo here)
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(miniAppId);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
        RestClient restClient = createRestClient(miniAppInfo.getBaseUrl());

        try {
            ResponseEntity<Map<String, Object>> response = restClient.delete()
                    .uri("/client")
                    .headers(outgoingHeaders -> {
                        if (dataTypeHeader != null) {
                            outgoingHeaders.add("Data-Type", dataTypeHeader);
                            System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
                        }
                    })
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (RestClientException e) {
            log.error("Failed to fetch data from mini-app {}: {}", miniAppId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    private RestClient createRestClient(String baseUrl) {
        return restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
}

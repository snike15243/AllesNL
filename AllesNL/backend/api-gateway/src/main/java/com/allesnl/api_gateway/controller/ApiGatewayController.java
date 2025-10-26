package com.allesnl.api_gateway.controller;


import com.allesnl.api_gateway.contracts.miniapp.MiniAppDetails;
import com.allesnl.api_gateway.contracts.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

// clients to call other services
@Configuration
class Clients {
  @Bean WebClient userClient() {
    return WebClient.builder().baseUrl("http://user-service:8080").build();
  }
  @Bean WebClient paymentClient() {
    return WebClient.builder().baseUrl("http://payment-service:8080").build();
  }
  @Bean WebClient miniAppClient() { return WebClient.builder().baseUrl("http://mini-app-service:8080").build(); }
}


@RestController
@RequestMapping("/api-gateway")
public class ApiGatewayController {

    private final WebClient userClient, paymentClient, miniAppClient;
    ApiGatewayController(WebClient userClient, WebClient paymentClient, WebClient miniAppClient) {
        this.userClient = userClient; this.paymentClient = paymentClient; this.miniAppClient = miniAppClient;
    }

    
    // Test endpoint
    @GetMapping("/hello")
    public Mono<String> hello() {
        return userClient.get()
            .uri("/users/hello")
            .retrieve()
            .bodyToMono(String.class);
    }


    /*
     * Gets user data from user service.
     * @input id - user ID
     * @output User object in JSON format
     */
    @GetMapping("/user/{id}")
    public Mono<User> getUserData(@PathVariable Long id) {
        
        return userClient.get()
        .uri("/users/" + id)
        .retrieve()
        .bodyToMono(User.class);
        // return ResponseEntity.ok(id.toString());
    }


    // Create a new user
    @PostMapping("/user")
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {

        // manually create a payload without the ID
        Map<String, Object> requestBody = Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "phoneNumber", user.getPhoneNumber(),
                "password", user.getPassword()
        );

        return userClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)  // ✅ directly use the map
                .retrieve()
                .toEntity(User.class);
    }

    // Login endpoint
    @PostMapping("/login")
    public Mono<ResponseEntity<Map>> login(@RequestBody Map<String, String> loginRequest) {
        return userClient.post()
                .uri("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .toEntity(Map.class);
    }


    /*
     * Deletes a user by ID.
     * @input id - user ID
     * @output
     */
    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
       
        return userClient.delete()
                .uri("/users/" + id)
                .retrieve()
                .toBodilessEntity();

    }

    @GetMapping("/payment")
    public Mono<String> getPaymentData(){
        return paymentClient.get()
                .uri("/hello")
                .retrieve()
                .bodyToMono(String.class);
    }

    /*
     * Gets mini-app data from mini-app service.
     * @input id - mini-app ID
     */
    @GetMapping("/miniapp/{id}")
    public Mono<ResponseEntity<byte[]>> getMiniAppData(@PathVariable Long id) {
        return miniAppClient.get()
                .uri("/miniapp/" + id + "/html")
                .retrieve()
                .toEntity(byte[].class);
//
//
//        String html = "<html><body><h1>Hello from Spring!</h1></body></html>";
//        return ResponseEntity.ok()
//                .contentType(MediaType.TEXT_HTML)
//                .body(html);
    }

    @PostMapping("/miniapp/{id}")
    public Mono<ResponseEntity<byte[]>> getMiniAppData(@PathVariable Long id, @RequestBody User user) {

        Map<String, Object> requestBody = Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "phoneNumber", user.getPhoneNumber(),
                "password", ""
        );

        return miniAppClient.post()
                .uri("/miniapp/" + id + "/html")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(byte[].class);
//
//
//        String html = "<html><body><h1>Hello from Spring!</h1></body></html>";
//        return ResponseEntity.ok()
//                .contentType(MediaType.TEXT_HTML)
//                .body(html);
    }

    /*
     * Gets all mini-apps metadata from mini-app service
     * @input
     * @output List of mini-app metadata in JSON format 
     */
    @GetMapping("/miniapp/all")
    public Mono<List<MiniAppDetails>> getAllMiniApps() {
        return miniAppClient.get()
                .uri("/miniapp/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    @GetMapping("payment/{userId}/{amount}")
    public Mono<ResponseEntity<Map>> getPaymentData(
            @PathVariable long userId,
            @PathVariable long amount
    ) {
        return miniAppClient.get()
                .uri("/miniapp/payment/{userId}/{amount}", userId, amount)
                .retrieve()
                .toEntity(Map.class);
    }



    @PostMapping("/miniapp/data/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> postClientData(@RequestBody Map<String, Object> incoming, @PathVariable Long id,
            // 1. Capture all headers from the incoming request.
            @RequestHeader HttpHeaders headers
    ) {
        System.out.println("Gateway received request for ID: " + id);

        // 2. Extract the specific header you want to forward.
        final String dataTypeHeader = headers.getFirst("Data-Type");

        return miniAppClient.post()
                .uri("/miniapp/{id}/data", id)
                // 3. Add the extracted header to the outgoing request.
                .headers(outgoingHeaders -> {
                    if (dataTypeHeader != null) {
                        outgoingHeaders.add("Data-Type", dataTypeHeader);
                        System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
                    }
                })
                .bodyValue(incoming)
                .exchangeToMono(clientResp -> {
                    HttpStatusCode status = clientResp.statusCode();
                    return clientResp.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                            .defaultIfEmpty(Map.of())
                            .map(body -> {
                                Map<String, Object> envelope = Map.of(
                                        "timestamp", OffsetDateTime.now().toString(),
                                        "service", "api-gateway",
                                        "status", status.value(),
                                        "data", body
                                );
                                return ResponseEntity.status(status).body(envelope);
                            });
                });
    }



    @GetMapping("/miniapp/data/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getClientData( @PathVariable Long id, @RequestHeader HttpHeaders headers
    ) {
        System.out.println("Gateway received request for ID: " + id);

        // 2. Extract the specific header you want to forward.
        final String dataTypeHeader = headers.getFirst("Data-Type");

        return miniAppClient.get()
                .uri("/miniapp/{id}/data", id)
                // 3. Add the extracted header to the outgoing request.
                .headers(outgoingHeaders -> {
                    if (dataTypeHeader != null) {
                        outgoingHeaders.add("Data-Type", dataTypeHeader);
                        System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
                    }
                })
                .exchangeToMono(clientResp -> {
                    HttpStatusCode status = clientResp.statusCode();
                    return clientResp.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                            .defaultIfEmpty(Map.of())
                            .map(body -> {
                                Map<String, Object> envelope = Map.of(
                                        "timestamp", OffsetDateTime.now().toString(),
                                        "service", "api-gateway",
                                        "status", status.value(),
                                        "data", body
                                );
                                return ResponseEntity.status(status).body(envelope);
                            });
                });
    }

    @DeleteMapping("/miniapp/data/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteClientData( @PathVariable Long id,
                                                                    // 1. Capture all headers from the incoming request.
                                                                    @RequestHeader HttpHeaders headers
    ) {
        System.out.println("Gateway received request for ID: " + id);

        // 2. Extract the specific header you want to forward.
        final String dataTypeHeader = headers.getFirst("Data-Type");

        return miniAppClient.delete()
                .uri("/miniapp/{id}/data", id)
                // 3. Add the extracted header to the outgoing request.
                .headers(outgoingHeaders -> {
                    if (dataTypeHeader != null) {
                        outgoingHeaders.add("Data-Type", dataTypeHeader);
                        System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
                    }
                })
                .exchangeToMono(clientResp -> {
                    HttpStatusCode status = clientResp.statusCode();
                    return clientResp.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                            .defaultIfEmpty(Map.of())
                            .map(body -> {
                                Map<String, Object> envelope = Map.of(
                                        "timestamp", OffsetDateTime.now().toString(),
                                        "service", "api-gateway",
                                        "status", status.value(),
                                        "data", body
                                );
                                return ResponseEntity.status(status).body(envelope);
                            });
                });
    }

}
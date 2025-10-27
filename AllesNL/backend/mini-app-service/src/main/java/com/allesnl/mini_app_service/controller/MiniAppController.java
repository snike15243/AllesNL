package com.allesnl.mini_app_service.controller;


import com.allesnl.mini_app_service.contracts.miniapp.MiniAppDetails;
import com.allesnl.mini_app_service.contracts.miniapp.MiniAppInfo;
import com.allesnl.mini_app_service.contracts.miniapp.MiniAppRequest;
import com.allesnl.mini_app_service.contracts.miniapp.MiniAppResponse;
import com.allesnl.mini_app_service.contracts.template.RegistrationRequest;
import com.allesnl.mini_app_service.contracts.template.RegistrationResponse;
import com.allesnl.mini_app_service.service.MiniAppClientService;
import com.allesnl.mini_app_service.service.MiniAppRegistryService;
import com.allesnl.mini_app_service.service.MiniAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import  com.allesnl.mini_app_service.contracts.user.User;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/miniapp")
@Slf4j
public class MiniAppController {

    private final MiniAppService miniAppService;
    private final MiniAppRegistryService miniAppRegistry;
    private final MiniAppClientService miniAppClientService;
//    private final WebClient userServiceClient;
//    private final WebClient paymentServiceClient;
//    private final WebClient APIGatewayClient;
//
//    public MiniAppController(MiniAppService miniAppService, MiniAppRegistryService miniAppRegistry,
//                             @Qualifier("userServiceClient") WebClient userServiceClient,
//                             @Qualifier("paymentServiceClient") WebClient paymentServiceClient,
//                             @Qualifier("APIGatewayClient") WebClient APIGatewayClient, MiniAppClientService miniAppClientService) {
//        this.miniAppService = miniAppService;
//        this.miniAppRegistry = miniAppRegistry;
//        this.userServiceClient = userServiceClient;
//        this.paymentServiceClient = paymentServiceClient;
//        this.APIGatewayClient = APIGatewayClient;
//        this.miniAppClientService = miniAppClientService;
//    }

    public MiniAppController(MiniAppService miniAppService, MiniAppRegistryService miniAppRegistry, MiniAppClientService miniAppClientService) {
        this.miniAppService = miniAppService;
        this.miniAppRegistry = miniAppRegistry;
        this.miniAppClientService = miniAppClientService;
    }

    @PostMapping
    public ResponseEntity<MiniAppResponse> makeMiniAppRequest(@RequestBody MiniAppRequest request) {
        MiniAppResponse response = miniAppService.makeRequest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{sagaId}")
    public ResponseEntity<MiniAppResponse> getMiniAppRequestStatus(@PathVariable String sagaId) {
        MiniAppResponse response = miniAppService.getRequestStatus(sagaId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerMiniApp(@RequestBody RegistrationRequest registration) {
        // First register in the registry
        log.info("Received Registration: {}", registration);
        MiniAppInfo miniAppInfo = miniAppRegistry.register(new MiniAppInfo((long) -1, registration.appName(), "", registration.baseUrl(), "", registration.authToken()));
        log.info("First Registration: {}", miniAppInfo);
        // Second get the details by calling the mini-app using the miniAppClientService
        Optional<MiniAppDetails> optionalMiniAppDetails = miniAppClientService.getMiniAppDetails(miniAppInfo.getAppId());
        if (optionalMiniAppDetails.isEmpty()) {
            // The provided baseUrl did not return the correct details.
            miniAppRegistry.unregister(miniAppInfo);
            return ResponseEntity.badRequest().build();
        }
        MiniAppDetails miniAppDetails = optionalMiniAppDetails.get();

        // Third update the registry with the new information
        miniAppInfo.setAppDescription(miniAppDetails.appDescription());
        miniAppInfo.setLogoUrl(miniAppDetails.logoUrl());
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.replace(miniAppInfo);

        if (optionalMiniAppInfo.isEmpty()) {
            // The registry could not update the miniAppInfo. This should not be possible.
            miniAppRegistry.unregister(miniAppInfo);
            return ResponseEntity.internalServerError().build();
        }
        RegistrationResponse response = new RegistrationResponse(optionalMiniAppInfo.get().getAppId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MiniAppDetails>> getAllMiniApps() {
        Collection<MiniAppInfo> miniApps = miniAppRegistry.getAll();
        return ResponseEntity.ok(miniApps.stream()
                .map(MiniAppInfo::toMiniAppDetails)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MiniAppDetails> getMiniAppInfo(@PathVariable Long id) {
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(id);
        if (optionalMiniAppInfo.isPresent()) {
            MiniAppInfo miniAppInfo = optionalMiniAppInfo.get();
            return ResponseEntity.ok(miniAppInfo.toMiniAppDetails());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/html")
    public ResponseEntity<byte[]> getMiniAppHtml(@PathVariable Long id) {
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(id);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return miniAppClientService.getMiniAppHtml(id);
    }

    @PostMapping("/{id}/html")
    public ResponseEntity<byte[]> getMiniAppHtml(@PathVariable Long id, @RequestBody User user) {
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(id);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return miniAppClientService.getMiniAppHtml(id, user);
    }

    @GetMapping("payment/{userId}/{amount}")
    public ResponseEntity<Map> getPaymentData(
            @PathVariable long userId,
            @PathVariable long amount
    ) {
        Map<String, String> map = new HashMap<>();
        map.put("amount", String.valueOf(amount));
        return ResponseEntity.ok(map);
    }


    @PostMapping("/{id}/data")
    public ResponseEntity<Map<String, Object>> postClientData( @RequestBody Map<String, Object> incoming, @PathVariable Long id,
    // 1. Capture all headers from the incoming request.
    @RequestHeader HttpHeaders headers){
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(id);
        log.info("id: {}, incoming: {}", id, incoming);
        log.info("Received MiniAppInfo: {}", optionalMiniAppInfo);

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return miniAppClientService.postClientData(id, incoming, dataTypeHeader);
    }



    @GetMapping("/{id}/data")
    public ResponseEntity<Map<String, Object>> getClientData( @PathVariable Long id,
                                                               // 1. Capture all headers from the incoming request.
                                                               @RequestHeader HttpHeaders headers){
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(id);
        log.info("id: {}, incoming: {}", id);
        log.info("Received MiniAppInfo: {}", optionalMiniAppInfo);

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return miniAppClientService.getClientData(id, dataTypeHeader);
    }

    @DeleteMapping("/{id}/data")
    public ResponseEntity<Map<String, Object>> deleteClientData( @PathVariable Long id,
                                                              // 1. Capture all headers from the incoming request.
                                                              @RequestHeader HttpHeaders headers){
        Optional<MiniAppInfo> optionalMiniAppInfo = miniAppRegistry.get(id);
        log.info("id: {}, incoming: {}", id);
        log.info("Received MiniAppInfo: {}", optionalMiniAppInfo);

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println("Forwarding header Data-Type: " + dataTypeHeader);
        if (optionalMiniAppInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return miniAppClientService.deleteClientData(id, dataTypeHeader);
    }

}
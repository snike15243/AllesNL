package nl.allesnl.custom.controller;

import nl.allesnl.custom.component.RegistrationState;
import nl.allesnl.custom.contracts.miniapp.MiniAppDetails;
import nl.allesnl.custom.service.ExternalClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;

@RestController
@RequestMapping("/internal")
class InternalController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.description}")
    private String appDescription;

    @Value("${app.logo-url}")
    private String logoUrl;

    private final RegistrationState registrationState;

    private final ExternalClientService externalClientService;

    InternalController(RegistrationState registrationState, ExternalClientService externalClientService){
        this.externalClientService = externalClientService;
        this.registrationState = registrationState;
    }

    @GetMapping("/details")
    ResponseEntity<MiniAppDetails> getDetails() {
        MiniAppDetails details = new MiniAppDetails(registrationState.getRegistrationId(), appName, appDescription, logoUrl);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/html")
    ResponseEntity<Resource> getHtml() {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @PostMapping("/html")
    ResponseEntity<Resource> getHtml(@RequestBody Map<String, String> user) {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @PostMapping("/client")
    public ResponseEntity<Map<String, Object>> postClientData(
            @RequestBody Map<String, Object> payload,
            @RequestHeader HttpHeaders headers) {

        Map<String, Object> res = new HashMap<>();
        String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println(payload.get("email"));
        switch (dataTypeHeader) {
            case "ticket": {
                Map<String, Object> ticketData = new HashMap<>();
                ticketData.put("email", payload.get("userEmail"));
                ticketData.put("time", LocalTime.now().toString());
                ticketData.put("origin", payload.get("origin"));
                ticketData.put("destination", payload.get("destination"));

                res.put("success", true);
                res.put("message", "Ticket created successfully");
                res.put("data", ticketData);
                break;
            }

            default:
                res.put("success", false);
                res.put("message", "Unknown Data-Type header: " + dataTypeHeader);
                break;
        }

        return ResponseEntity.ok(res);
    }


    @GetMapping("/client")
    ResponseEntity<Map<String, Object>> getClientData(@RequestHeader HttpHeaders headers) {

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println(dataTypeHeader);
        Map<String, Object> map = new HashMap<>();
        map.put("Message", "Get");

        return ResponseEntity.ok(map);
    }

    @DeleteMapping("/client")
    ResponseEntity<Map<String, Object>> deleteClientData(@RequestHeader HttpHeaders headers) {

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println(dataTypeHeader);
        Map<String, Object> map = new HashMap<>();
        map.put("Message", "Delete");

        return ResponseEntity.ok(map);
    }


}

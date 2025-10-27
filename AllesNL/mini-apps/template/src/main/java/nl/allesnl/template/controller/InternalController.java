package nl.allesnl.template.controller;

import nl.allesnl.template.component.RegistrationState;
import nl.allesnl.template.contracts.miniapp.MiniAppDetails;
import nl.allesnl.template.record.Quote;
import nl.allesnl.template.service.ExternalClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


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
        for(String s : user.keySet()){
            System.out.println(s);
        }
        ClassPathResource resource = new ClassPathResource("static/index.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @PostMapping("/client")
    ResponseEntity<Map<String, Object>> postClientData(@RequestBody Map<String, Object> payload, @RequestHeader HttpHeaders headers) {

        final String dataTypeHeader = headers.getFirst("Data-Type");
        Map<String, Object> res = new HashMap<>();
        switch (dataTypeHeader) {
            case "user":
                Quote quote = externalClientService.getQuote();
                Object user = payload.get("user");

                System.out.println(user);
                res.put("author", quote);

                Map<String, Object> userMap = (Map<String, Object>) payload.get("user");

                if (userMap != null) {
                    String userName = (String) userMap.get("name");
                    res.put("quote", new Quote(0, quote.quote(),  userName) );
                }
                break;
            default:
                res.put("error", "User error");
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/client")
    ResponseEntity<Map<String, Object>> getClientData(@RequestHeader HttpHeaders headers) {

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println(dataTypeHeader);
        Map<String, Object> res = new HashMap<>();
        switch (dataTypeHeader) {
            case "quote":
                Quote quote = externalClientService.getQuote();
                res.put("quote", quote);
                break;
            default:
                res.put("error", "Unknown Data-Type");
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/client")
    ResponseEntity<Map<String, Object>> deleteClientData(@RequestHeader HttpHeaders headers) {

        final String dataTypeHeader = headers.getFirst("Data-Type");
        System.out.println(dataTypeHeader);
        Map<String, Object> map = new HashMap<>();
        map.put("Message", "Delete");

        return ResponseEntity.ok(map);
    }

    @GetMapping("/quote")
    ResponseEntity<Quote> getQuote() {
        Quote quote =  externalClientService.getQuote();
        return ResponseEntity.ok(quote);
    }

}

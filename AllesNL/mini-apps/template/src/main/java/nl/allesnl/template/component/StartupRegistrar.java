package nl.allesnl.template.component;

import nl.allesnl.template.contracts.template.RegistrationResponse;
import nl.allesnl.template.service.InternalClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class StartupRegistrar {

    private final InternalClientService internalClientService;
    private final RegistrationState registrationState;

    @Value("${spring.application.name}")
    private String appName;

    public StartupRegistrar(InternalClientService internalClientService, RegistrationState registrationState) {
        this.internalClientService = internalClientService;
        this.registrationState = registrationState;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        int maxRetries = 10;
        int retry = 1;
        Exception failure = null;
        while (retry <= maxRetries) {
            System.out.println("Registration try " + retry + " from max " + maxRetries);
            try {
                RegistrationResponse response = internalClientService.register(appName, "http://" + appName + ":8080/internal");
                registrationState.setRegistrationId(response.registrationId());
                System.out.println("Registered with registrationId " + response.registrationId());
                return;
            } catch (Exception e) {
                registrationState.setRegistrationId(-1);
                failure = e;
            }
            retry++;
        }
        System.err.println("Failed to register: " + failure.getMessage());
    }

}

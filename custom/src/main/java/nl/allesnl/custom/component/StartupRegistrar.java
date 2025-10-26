package nl.allesnl.custom.component;

import nl.allesnl.custom.contracts.template.RegistrationResponse;
import nl.allesnl.custom.service.InternalClientService;
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
        try {
            RegistrationResponse response = internalClientService.register(appName, "http://" + appName + ":8080/internal");
            registrationState.setRegistrationId(response.registrationId());
            System.out.println("Registered with registrationId " + response.registrationId());
        } catch (Exception e) {
            registrationState.setRegistrationId(-1);
            System.err.println("Failed to register: " + e.getMessage());
        }
    }

}

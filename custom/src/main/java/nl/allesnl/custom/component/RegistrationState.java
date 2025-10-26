package nl.allesnl.custom.component;

import org.springframework.stereotype.Component;

@Component
public class RegistrationState {

    private long registrationId;

    public long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(long registrationId) {
        this.registrationId = registrationId;
    }

}

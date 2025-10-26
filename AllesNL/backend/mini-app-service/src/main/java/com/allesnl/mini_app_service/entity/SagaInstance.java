package com.allesnl.mini_app_service.entity;

import com.allesnl.mini_app_service.enumerations.SagaState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SagaInstance {

    @Id
    private String sagaId;

    @Enumerated(EnumType.STRING)
    private SagaState state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String sagaData;

    @PrePersist // run before the first time the saga is saved to the database
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (state == null) {
            state = SagaState.STARTED;
        }
    }

    @PreUpdate // run on every update
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void advanceTo(SagaState newState) {
        this.state = newState;
        this.updatedAt = LocalDateTime.now();
    }
}

package com.allesnl.mini_app_service.service;

import com.allesnl.mini_app_service.contracts.miniapp.MiniAppRequest;
import com.allesnl.mini_app_service.entity.SagaInstance;
import com.allesnl.mini_app_service.enumerations.SagaState;
import com.allesnl.mini_app_service.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final SagaInstanceRepository sagaRepository;
//    private final UserServiceClient userClient;
//    private final PaymentServiceClient paymentClient;

    @Transactional
    public SagaInstance startPaymentSaga(MiniAppRequest request) {
        String sagaId = UUID.randomUUID().toString();

        SagaInstance saga = null;
        try {
            // Create saga instance
            saga = new SagaInstance();
            saga.setSagaId(sagaId);
            saga.setState(SagaState.STARTED);
            saga.setSagaData("Dummy data");

            saga = sagaRepository.save(saga);

            // Process saga steps
            //makePaymentSaga(saga);

            return saga;

        } catch (Exception e) {
            updateSagaState(saga, SagaState.FAILED);
            return null;
        }
    }

    private void updateSagaState(SagaInstance saga, SagaState newState) {
        saga.setState(newState);
        sagaRepository.save(saga);
    }
}
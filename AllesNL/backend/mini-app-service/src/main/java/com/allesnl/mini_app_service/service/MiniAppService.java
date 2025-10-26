package com.allesnl.mini_app_service.service;

import com.allesnl.mini_app_service.contracts.miniapp.MiniAppRequest;
import com.allesnl.mini_app_service.contracts.miniapp.MiniAppResponse;
import com.allesnl.mini_app_service.entity.SagaInstance;
import com.allesnl.mini_app_service.enumerations.SagaState;
import com.allesnl.mini_app_service.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MiniAppService {

    private final SagaOrchestrator sagaOrchestrator;
    private final SagaInstanceRepository sagaRepository;
    private final MiniAppRegistryService miniAppRegistry;

    private boolean requiresSaga(MiniAppRequest request) {
        // Define logic to determine if the request needs a saga
        return true;
    }

    public MiniAppResponse makeRequest(MiniAppRequest request) {
        if(requiresSaga(request)) {
            SagaInstance saga = sagaOrchestrator.startPaymentSaga(request);
            String response = "data";
            return MiniAppResponse.success(response);
        }
        // Todo: handle non-saga requests: pass the message to mini-app through plug-in (adapter)
        else {
            String response = "data";
            return MiniAppResponse.success(response);
        }
    }

    public MiniAppResponse getRequestStatus(String sagaId) {
        Optional<SagaInstance> sagaOpt = sagaRepository.findById(sagaId);

        if (sagaOpt.isEmpty()) {
            return MiniAppResponse.failure();
        }

        SagaInstance saga = sagaOpt.get();

        // Todo: set the fields of response based on saga

        return MiniAppResponse.success("data");
    }

    private String getStatusMessage(SagaState state) {
        return switch (state) {
            case STARTED -> "Payment processing started";
            case COMPLETED -> "Payment and reservation completed successfully";
            case FAILED -> "Saga failed after retries";
            default -> "Unknown state";
        };
    }
}
























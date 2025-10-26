package com.allesnl.mini_app_service.enumerations;

public enum SagaState {
    STARTED,
    ROUTE_RESERVED,
    PAYMENT_PROCESSED,
    BOOKING_CONFIRMED,
    COMPLETED,
    COMPENSATING_PAYMENT,
    COMPENSATING_RESERVATION,
    COMPENSATION_COMPLETED,
    FAILED
}
package com.allesnl.payment_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World, this is Payment Service.";
    }
}
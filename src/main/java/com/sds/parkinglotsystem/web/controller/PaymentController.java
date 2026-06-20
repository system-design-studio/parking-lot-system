package com.sds.parkinglotsystem.web.controller;

import com.sds.parkinglotsystem.service.PaymentService;
import com.sds.parkinglotsystem.web.dto.PaymentRequest;
import com.sds.parkinglotsystem.web.dto.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment API. Settling a ticket releases its spot back to the pool.
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/tickets/{ticketNumber}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@PathVariable String ticketNumber, @Valid @RequestBody PaymentRequest request) {
        return PaymentResponse.from(paymentService.pay(ticketNumber, request.method()));
    }
}

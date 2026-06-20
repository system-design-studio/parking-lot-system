package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.PaymentMethod;
import com.sds.parkinglotsystem.domain.enums.PaymentStatus;
import com.sds.parkinglotsystem.domain.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        String ticketNumber,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        Instant paidAt) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getTicket().getTicketNumber(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getPaidAt());
    }
}

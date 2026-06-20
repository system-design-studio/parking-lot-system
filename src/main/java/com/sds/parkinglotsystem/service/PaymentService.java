package com.sds.parkinglotsystem.service;

import com.sds.parkinglotsystem.domain.enums.PaymentMethod;
import com.sds.parkinglotsystem.domain.enums.PaymentStatus;
import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import com.sds.parkinglotsystem.domain.model.Payment;
import com.sds.parkinglotsystem.domain.model.Ticket;
import com.sds.parkinglotsystem.exception.InvalidParkingStateException;
import com.sds.parkinglotsystem.exception.ResourceNotFoundException;
import com.sds.parkinglotsystem.repository.PaymentRepository;
import com.sds.parkinglotsystem.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * Settles the amount due on a ticket. On success the ticket becomes PAID and the
 * occupied spot is released back to the pool.
 */
@Service
public class PaymentService {

    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final Clock clock;

    public PaymentService(TicketRepository ticketRepository,
                          PaymentRepository paymentRepository,
                          Clock clock) {
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.clock = clock;
    }

    @Transactional
    public Payment pay(String ticketNumber, PaymentMethod method) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> ResourceNotFoundException.of("Ticket", ticketNumber));

        if (ticket.getStatus() != TicketStatus.AWAITING_PAYMENT) {
            throw new InvalidParkingStateException(
                    "Ticket %s is not awaiting payment (status=%s)"
                            .formatted(ticketNumber, ticket.getStatus()));
        }

        Payment payment = new Payment(
                ticket, ticket.getAmount(), method, PaymentStatus.COMPLETED, clock.instant());

        ticket.setStatus(TicketStatus.PAID);
        ticket.getSpot().free();

        return paymentRepository.save(payment);
    }
}

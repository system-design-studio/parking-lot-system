package com.sds.parkinglotsystem.service;

import com.sds.parkinglotsystem.domain.enums.PaymentMethod;
import com.sds.parkinglotsystem.domain.enums.PaymentStatus;
import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import com.sds.parkinglotsystem.domain.enums.VehicleType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import com.sds.parkinglotsystem.domain.model.Payment;
import com.sds.parkinglotsystem.domain.model.Ticket;
import com.sds.parkinglotsystem.domain.model.Vehicle;
import com.sds.parkinglotsystem.exception.InvalidParkingStateException;
import com.sds.parkinglotsystem.repository.PaymentRepository;
import com.sds.parkinglotsystem.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final Instant NOW = Instant.parse("2026-06-07T12:00:00Z");

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        paymentService = new PaymentService(ticketRepository, paymentRepository, clock);
    }

    private static Ticket awaitingTicket(BigDecimal amount) {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotType(SpotType.COMPACT);
        spot.occupy();
        Ticket ticket = new Ticket("TKT-1", new Vehicle("KA01AB1234", VehicleType.CAR), spot, NOW.minusSeconds(3600));
        ticket.setExitTime(NOW);
        ticket.setAmount(amount);
        ticket.setStatus(TicketStatus.AWAITING_PAYMENT);
        return ticket;
    }

    @Test
    void payCompletesPaymentMarksPaidAndFreesSpot() {
        Ticket ticket = awaitingTicket(new BigDecimal("40"));
        when(ticketRepository.findByTicketNumber("TKT-1")).thenReturn(Optional.of(ticket));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.pay("TKT-1", PaymentMethod.CARD);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getAmount()).isEqualByComparingTo("40");
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPaidAt()).isEqualTo(NOW);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.PAID);
        assertThat(ticket.getSpot().getStatus()).isEqualTo(SpotStatus.AVAILABLE);
    }

    @Test
    void payRejectsTicketNotAwaitingPayment() {
        Ticket ticket = awaitingTicket(new BigDecimal("40"));
        ticket.setStatus(TicketStatus.ACTIVE);
        when(ticketRepository.findByTicketNumber("TKT-1")).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> paymentService.pay("TKT-1", PaymentMethod.CASH))
                .isInstanceOf(InvalidParkingStateException.class);

        verify(paymentRepository, never()).save(any());
    }
}

package com.sds.parkinglotsystem.repository;

import com.sds.parkinglotsystem.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTicket_TicketNumber(String ticketNumber);
}

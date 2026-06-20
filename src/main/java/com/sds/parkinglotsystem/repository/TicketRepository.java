package com.sds.parkinglotsystem.repository;

import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import com.sds.parkinglotsystem.domain.model.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Eagerly fetches the vehicle and spot so the ticket can be mapped to a
     * response after the transaction closes (open-in-view is disabled).
     */
    @EntityGraph(attributePaths = {"vehicle", "spot"})
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    boolean existsByVehicle_LicensePlateAndStatusIn(String licensePlate,
                                                    Collection<TicketStatus> statuses);
}

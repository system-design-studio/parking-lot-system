package com.sds.parkinglotsystem.service;

import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import com.sds.parkinglotsystem.domain.enums.VehicleType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import com.sds.parkinglotsystem.domain.model.Ticket;
import com.sds.parkinglotsystem.domain.model.Vehicle;
import com.sds.parkinglotsystem.exception.InvalidParkingStateException;
import com.sds.parkinglotsystem.exception.NoSpotAvailableException;
import com.sds.parkinglotsystem.exception.ResourceNotFoundException;
import com.sds.parkinglotsystem.repository.TicketRepository;
import com.sds.parkinglotsystem.repository.VehicleRepository;
import com.sds.parkinglotsystem.strategy.pricing.PricingStrategy;
import com.sds.parkinglotsystem.strategy.spot.SpotAssignmentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Core entry/exit flow. Entry atomically claims a spot and issues a ticket;
 * exit computes the fee and moves the ticket to AWAITING_PAYMENT. The spot is
 * only freed once payment completes (see {@link PaymentService}).
 */
@Service
public class ParkingService {

    private final SpotAssignmentStrategy spotAssignmentStrategy;
    private final PricingStrategy pricingStrategy;
    private final TicketRepository ticketRepository;
    private final VehicleRepository vehicleRepository;
    private final Clock clock;

    public ParkingService(SpotAssignmentStrategy spotAssignmentStrategy,
                          PricingStrategy pricingStrategy,
                          TicketRepository ticketRepository,
                          VehicleRepository vehicleRepository,
                          Clock clock) {
        this.spotAssignmentStrategy = spotAssignmentStrategy;
        this.pricingStrategy = pricingStrategy;
        this.ticketRepository = ticketRepository;
        this.vehicleRepository = vehicleRepository;
        this.clock = clock;
    }

    /**
     * Admits a vehicle: claims the best-fit available spot, marks it occupied and
     * returns a fresh ACTIVE ticket.
     *
     * @throws InvalidParkingStateException if the vehicle is already parked
     * @throws NoSpotAvailableException     if the lot is full for this vehicle type
     */
    @Transactional
    public Ticket parkVehicle(Long lotId, String licensePlate, VehicleType vehicleType) {
        String plate = normalizePlate(licensePlate);

        if (ticketRepository.existsByVehicle_LicensePlateAndStatusIn(plate, TicketStatus.OPEN_STATUSES)) {
            throw new InvalidParkingStateException(
                    "Vehicle %s is already parked".formatted(plate));
        }

        ParkingSpot spot = spotAssignmentStrategy.assignSpot(lotId, vehicleType)
                .orElseThrow(() -> new NoSpotAvailableException(
                        "No available spot for vehicle type %s in lot %d".formatted(vehicleType, lotId)));
        spot.occupy();

        Vehicle vehicle = resolveVehicle(plate, vehicleType);
        Ticket ticket = new Ticket(generateTicketNumber(), vehicle, spot, clock.instant());
        return ticketRepository.save(ticket);
    }

    /**
     * Initiates exit: stamps the exit time, computes the fee and moves the ticket
     * to AWAITING_PAYMENT. Idempotent-ish: calling on an already-awaiting ticket
     * is rejected so a re-scan does not recompute a different fee.
     */
    @Transactional
    public Ticket initiateExit(String ticketNumber) {
        Ticket ticket = requireTicket(ticketNumber);
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new InvalidParkingStateException(
                    "Ticket %s is not active (status=%s)".formatted(ticketNumber, ticket.getStatus()));
        }

        Instant exitTime = clock.instant();
        ticket.setExitTime(exitTime);
        ticket.setAmount(pricingStrategy.calculateFee(ticket, exitTime));
        ticket.setStatus(TicketStatus.AWAITING_PAYMENT);
        return ticket;
    }

    @Transactional(readOnly = true)
    public Ticket getTicket(String ticketNumber) {
        return requireTicket(ticketNumber);
    }

    private Ticket requireTicket(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> ResourceNotFoundException.of("Ticket", ticketNumber));
    }

    private Vehicle resolveVehicle(String plate, VehicleType vehicleType) {
        return vehicleRepository.findByLicensePlate(plate)
                .orElseGet(() -> vehicleRepository.save(new Vehicle(plate, vehicleType)));
    }

    private static String normalizePlate(String licensePlate) {
        return licensePlate == null ? null : licensePlate.trim().toUpperCase();
    }

    private static String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}

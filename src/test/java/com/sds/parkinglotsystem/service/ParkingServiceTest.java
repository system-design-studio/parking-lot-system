package com.sds.parkinglotsystem.service;

import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import com.sds.parkinglotsystem.domain.enums.VehicleType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import com.sds.parkinglotsystem.domain.model.Ticket;
import com.sds.parkinglotsystem.domain.model.Vehicle;
import com.sds.parkinglotsystem.exception.InvalidParkingStateException;
import com.sds.parkinglotsystem.exception.NoSpotAvailableException;
import com.sds.parkinglotsystem.repository.TicketRepository;
import com.sds.parkinglotsystem.repository.VehicleRepository;
import com.sds.parkinglotsystem.strategy.pricing.PricingStrategy;
import com.sds.parkinglotsystem.strategy.spot.SpotAssignmentStrategy;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    private static final Instant NOW = Instant.parse("2026-06-07T10:00:00Z");

    @Mock
    private SpotAssignmentStrategy spotAssignmentStrategy;
    @Mock
    private PricingStrategy pricingStrategy;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private VehicleRepository vehicleRepository;

    private ParkingService parkingService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        parkingService = new ParkingService(
                spotAssignmentStrategy, pricingStrategy, ticketRepository, vehicleRepository, clock);
    }

    private static ParkingSpot availableSpot(SpotType type) {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotType(type);
        spot.setStatus(SpotStatus.AVAILABLE);
        return spot;
    }

    @Test
    void parkVehicleClaimsSpotIssuesActiveTicketAndNormalizesPlate() {
        ParkingSpot spot = availableSpot(SpotType.COMPACT);
        when(spotAssignmentStrategy.assignSpot(1L, VehicleType.CAR)).thenReturn(Optional.of(spot));
        when(ticketRepository.existsByVehicle_LicensePlateAndStatusIn(eq("KA01AB1234"), any()))
                .thenReturn(false);
        when(vehicleRepository.findByLicensePlate("KA01AB1234")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket ticket = parkingService.parkVehicle(1L, " ka01ab1234 ", VehicleType.CAR);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.ACTIVE);
        assertThat(ticket.getEntryTime()).isEqualTo(NOW);
        assertThat(ticket.getTicketNumber()).startsWith("TKT-");
        assertThat(ticket.getVehicle().getLicensePlate()).isEqualTo("KA01AB1234");
        assertThat(spot.getStatus()).isEqualTo(SpotStatus.OCCUPIED);
    }

    @Test
    void parkVehicleRejectsAlreadyParkedVehicle() {
        when(ticketRepository.existsByVehicle_LicensePlateAndStatusIn(eq("KA01AB1234"), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> parkingService.parkVehicle(1L, "KA01AB1234", VehicleType.CAR))
                .isInstanceOf(InvalidParkingStateException.class);

        verify(spotAssignmentStrategy, never()).assignSpot(any(), any());
    }

    @Test
    void parkVehicleThrowsWhenLotFull() {
        when(ticketRepository.existsByVehicle_LicensePlateAndStatusIn(anyString(), any()))
                .thenReturn(false);
        when(spotAssignmentStrategy.assignSpot(1L, VehicleType.TRUCK)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingService.parkVehicle(1L, "KA01AB1234", VehicleType.TRUCK))
                .isInstanceOf(NoSpotAvailableException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void initiateExitComputesFeeAndMovesToAwaitingPayment() {
        ParkingSpot spot = availableSpot(SpotType.COMPACT);
        spot.occupy();
        Vehicle vehicle = new Vehicle("KA01AB1234", VehicleType.CAR);
        Ticket ticket = new Ticket("TKT-1", vehicle, spot, NOW.minusSeconds(3600));
        when(ticketRepository.findByTicketNumber("TKT-1")).thenReturn(Optional.of(ticket));
        when(pricingStrategy.calculateFee(ticket, NOW)).thenReturn(new BigDecimal("20"));

        Ticket result = parkingService.initiateExit("TKT-1");

        assertThat(result.getStatus()).isEqualTo(TicketStatus.AWAITING_PAYMENT);
        assertThat(result.getExitTime()).isEqualTo(NOW);
        assertThat(result.getAmount()).isEqualByComparingTo("20");
    }

    @Test
    void initiateExitRejectsNonActiveTicket() {
        ParkingSpot spot = availableSpot(SpotType.COMPACT);
        Ticket ticket = new Ticket("TKT-1", new Vehicle("P", VehicleType.CAR), spot, NOW);
        ticket.setStatus(TicketStatus.PAID);
        when(ticketRepository.findByTicketNumber("TKT-1")).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> parkingService.initiateExit("TKT-1"))
                .isInstanceOf(InvalidParkingStateException.class);
    }
}

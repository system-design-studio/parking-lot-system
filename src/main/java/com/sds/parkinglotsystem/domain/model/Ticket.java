package com.sds.parkinglotsystem.domain.model;

import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
public class Ticket extends BaseEntity {

    @Column(name = "ticket_number", nullable = false, unique = true, length = 40)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot spot;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "exit_time")
    private Instant exitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.ACTIVE;

    /** Amount due, populated when exit is initiated. */
    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    public Ticket(String ticketNumber, Vehicle vehicle, ParkingSpot spot, Instant entryTime) {
        this.ticketNumber = ticketNumber;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
        this.status = TicketStatus.ACTIVE;
    }
}

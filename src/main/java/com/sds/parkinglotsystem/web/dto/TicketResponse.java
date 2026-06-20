package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.enums.TicketStatus;
import com.sds.parkinglotsystem.domain.enums.VehicleType;
import com.sds.parkinglotsystem.domain.model.Ticket;

import java.math.BigDecimal;
import java.time.Instant;

public record TicketResponse(
        String ticketNumber,
        String licensePlate,
        VehicleType vehicleType,
        String spotNumber,
        SpotType spotType,
        Instant entryTime,
        Instant exitTime,
        TicketStatus status,
        BigDecimal amount) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getTicketNumber(),
                ticket.getVehicle().getLicensePlate(),
                ticket.getVehicle().getVehicleType(),
                ticket.getSpot().getSpotNumber(),
                ticket.getSpot().getSpotType(),
                ticket.getEntryTime(),
                ticket.getExitTime(),
                ticket.getStatus(),
                ticket.getAmount());
    }
}

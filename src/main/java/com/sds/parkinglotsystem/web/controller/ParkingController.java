package com.sds.parkinglotsystem.web.controller;

import com.sds.parkinglotsystem.service.ParkingService;
import com.sds.parkinglotsystem.web.dto.TicketResponse;
import com.sds.parkinglotsystem.web.dto.VehicleEntryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Vehicle entry/exit API.
 */
@RestController
@RequestMapping("/api/v1")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    /** Admit a vehicle and issue a ticket. */
    @PostMapping("/lots/{lotId}/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse enter(@PathVariable Long lotId, @Valid @RequestBody VehicleEntryRequest request) {
        return TicketResponse.from(
                parkingService.parkVehicle(lotId, request.licensePlate(), request.vehicleType()));
    }

    /** Initiate exit: stamps exit time and computes the fee due. */
    @PostMapping("/tickets/{ticketNumber}/exit")
    public TicketResponse exit(@PathVariable String ticketNumber) {
        return TicketResponse.from(parkingService.initiateExit(ticketNumber));
    }

    @GetMapping("/tickets/{ticketNumber}")
    public TicketResponse getTicket(@PathVariable String ticketNumber) {
        return TicketResponse.from(parkingService.getTicket(ticketNumber));
    }
}

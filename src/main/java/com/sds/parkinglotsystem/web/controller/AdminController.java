package com.sds.parkinglotsystem.web.controller;

import com.sds.parkinglotsystem.service.AvailabilityService;
import com.sds.parkinglotsystem.service.ParkingLotAdminService;
import com.sds.parkinglotsystem.web.dto.AddFloorRequest;
import com.sds.parkinglotsystem.web.dto.AddSpotsRequest;
import com.sds.parkinglotsystem.web.dto.AvailabilityResponse;
import com.sds.parkinglotsystem.web.dto.CreateLotRequest;
import com.sds.parkinglotsystem.web.dto.FloorResponse;
import com.sds.parkinglotsystem.web.dto.LotResponse;
import com.sds.parkinglotsystem.web.dto.SpotResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrative API for provisioning lots, floors and spots, and for reading
 * live availability.
 */
@RestController
@RequestMapping("/api/v1")
public class AdminController {

    private final ParkingLotAdminService adminService;
    private final AvailabilityService availabilityService;

    public AdminController(ParkingLotAdminService adminService,
                           AvailabilityService availabilityService) {
        this.adminService = adminService;
        this.availabilityService = availabilityService;
    }

    @PostMapping("/lots")
    @ResponseStatus(HttpStatus.CREATED)
    public LotResponse createLot(@Valid @RequestBody CreateLotRequest request) {
        return LotResponse.from(adminService.createLot(request.name(), request.address()));
    }

    @GetMapping("/lots/{lotId}")
    public LotResponse getLot(@PathVariable Long lotId) {
        return LotResponse.from(adminService.getLot(lotId));
    }

    @PostMapping("/lots/{lotId}/floors")
    @ResponseStatus(HttpStatus.CREATED)
    public FloorResponse addFloor(@PathVariable Long lotId, @Valid @RequestBody AddFloorRequest request) {
        return FloorResponse.from(adminService.addFloor(lotId, request.floorNumber(), request.name()));
    }

    @PostMapping("/floors/{floorId}/spots")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SpotResponse> addSpots(@PathVariable Long floorId, @Valid @RequestBody AddSpotsRequest request) {
        return adminService.addSpots(floorId, request.spotCounts()).stream()
                .map(SpotResponse::from)
                .toList();
    }

    @GetMapping("/lots/{lotId}/availability")
    public ResponseEntity<AvailabilityResponse> availability(@PathVariable Long lotId) {
        return ResponseEntity.ok(
                AvailabilityResponse.of(lotId, availabilityService.availableByType(lotId)));
    }
}

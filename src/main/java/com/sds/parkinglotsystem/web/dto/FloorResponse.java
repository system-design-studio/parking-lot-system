package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.model.ParkingFloor;

public record FloorResponse(Long id, Long lotId, int floorNumber, String name) {

    public static FloorResponse from(ParkingFloor floor) {
        return new FloorResponse(
                floor.getId(),
                floor.getParkingLot().getId(),
                floor.getFloorNumber(),
                floor.getName());
    }
}

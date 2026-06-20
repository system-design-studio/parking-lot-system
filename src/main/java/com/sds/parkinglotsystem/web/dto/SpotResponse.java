package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;

public record SpotResponse(Long id, String spotNumber, SpotType spotType, SpotStatus status) {

    public static SpotResponse from(ParkingSpot spot) {
        return new SpotResponse(spot.getId(), spot.getSpotNumber(), spot.getSpotType(), spot.getStatus());
    }
}

package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.model.ParkingLot;

public record LotResponse(Long id, String name, String address) {

    public static LotResponse from(ParkingLot lot) {
        return new LotResponse(lot.getId(), lot.getName(), lot.getAddress());
    }
}

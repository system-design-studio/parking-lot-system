package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleEntryRequest(
        @NotBlank String licensePlate,
        @NotNull VehicleType vehicleType) {
}

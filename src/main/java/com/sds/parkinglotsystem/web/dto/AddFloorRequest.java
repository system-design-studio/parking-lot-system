package com.sds.parkinglotsystem.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AddFloorRequest(
        @Positive int floorNumber,
        @NotBlank String name) {
}

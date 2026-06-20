package com.sds.parkinglotsystem.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLotRequest(
        @NotBlank String name,
        @NotBlank String address) {
}

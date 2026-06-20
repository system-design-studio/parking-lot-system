package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.SpotType;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record AddSpotsRequest(
        @NotEmpty Map<SpotType, Integer> spotCounts) {
}

package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.SpotType;

import java.util.Map;

public record AvailabilityResponse(
        Long lotId,
        long totalAvailable,
        Map<SpotType, Long> availableByType) {

    public static AvailabilityResponse of(Long lotId, Map<SpotType, Long> availableByType) {
        long total = availableByType.values().stream().mapToLong(Long::longValue).sum();
        return new AvailabilityResponse(lotId, total, availableByType);
    }
}

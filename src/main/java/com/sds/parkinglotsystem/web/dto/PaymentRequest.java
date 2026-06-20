package com.sds.parkinglotsystem.web.dto;

import com.sds.parkinglotsystem.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull PaymentMethod method) {
}

package com.sds.parkinglotsystem.strategy.pricing;

import com.sds.parkinglotsystem.domain.model.Ticket;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Strategy for computing the fee owed for a parking session.
 */
public interface PricingStrategy {

    /**
     * @param ticket   the active ticket (carries entry time and spot type)
     * @param exitTime the moment the vehicle is leaving
     * @return the fee due, never negative
     */
    BigDecimal calculateFee(Ticket ticket, Instant exitTime);
}

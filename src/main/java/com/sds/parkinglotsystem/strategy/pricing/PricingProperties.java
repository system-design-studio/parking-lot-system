package com.sds.parkinglotsystem.strategy.pricing;

import com.sds.parkinglotsystem.domain.enums.SpotType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Externalised pricing configuration, bound from {@code parking.pricing.*}.
 */
@ConfigurationProperties(prefix = "parking.pricing")
public class PricingProperties {

    /** Flat fee applied to every parking session, on top of hourly charges. */
    private BigDecimal baseFee = BigDecimal.ZERO;

    /** Grace period; sessions shorter than this are charged only the base fee. */
    private int freeMinutes = 0;

    /** Per-hour rate keyed by spot type. */
    private Map<SpotType, BigDecimal> hourlyRates = new EnumMap<>(SpotType.class);

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    public int getFreeMinutes() {
        return freeMinutes;
    }

    public void setFreeMinutes(int freeMinutes) {
        this.freeMinutes = freeMinutes;
    }

    public Map<SpotType, BigDecimal> getHourlyRates() {
        return hourlyRates;
    }

    public void setHourlyRates(Map<SpotType, BigDecimal> hourlyRates) {
        this.hourlyRates = hourlyRates;
    }

    public BigDecimal rateFor(SpotType spotType) {
        BigDecimal rate = hourlyRates.get(spotType);
        if (rate == null) {
            throw new IllegalStateException("No hourly rate configured for spot type " + spotType);
        }
        return rate;
    }
}

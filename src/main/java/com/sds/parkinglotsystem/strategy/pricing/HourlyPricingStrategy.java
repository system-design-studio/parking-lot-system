package com.sds.parkinglotsystem.strategy.pricing;

import com.sds.parkinglotsystem.domain.model.Ticket;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

/**
 * Charges {@code baseFee + ceil(hours) * hourlyRate(spotType)}. Sessions within
 * the configured grace period are charged only the base fee. Partial hours round
 * up, which is the conventional behaviour for parking garages.
 */
@Component
public class HourlyPricingStrategy implements PricingStrategy {

    private final PricingProperties properties;

    public HourlyPricingStrategy(PricingProperties properties) {
        this.properties = properties;
    }

    @Override
    public BigDecimal calculateFee(Ticket ticket, Instant exitTime) {
        Instant entryTime = ticket.getEntryTime();
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("exitTime cannot be before entryTime");
        }

        Duration stay = Duration.between(entryTime, exitTime);
        long minutes = stay.toMinutes();
        if (minutes <= properties.getFreeMinutes()) {
            return properties.getBaseFee();
        }

        long billableHours = (stay.getSeconds() + 3599) / 3600; // ceil to whole hours
        BigDecimal rate = properties.rateFor(ticket.getSpot().getSpotType());
        return properties.getBaseFee().add(rate.multiply(BigDecimal.valueOf(billableHours)));
    }
}

package com.sds.parkinglotsystem.strategy.pricing;

import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import com.sds.parkinglotsystem.domain.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HourlyPricingStrategyTest {

    private static final Instant ENTRY = Instant.parse("2026-06-07T08:00:00Z");

    private HourlyPricingStrategy strategy;

    @BeforeEach
    void setUp() {
        Map<SpotType, BigDecimal> rates = new EnumMap<>(SpotType.class);
        rates.put(SpotType.COMPACT, new BigDecimal("20"));
        rates.put(SpotType.LARGE, new BigDecimal("30"));

        PricingProperties props = new PricingProperties();
        props.setBaseFee(new BigDecimal("5"));
        props.setFreeMinutes(15);
        props.setHourlyRates(rates);

        strategy = new HourlyPricingStrategy(props);
    }

    private static Ticket ticketOnSpot(SpotType type) {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotType(type);
        Ticket ticket = new Ticket();
        ticket.setSpot(spot);
        ticket.setEntryTime(ENTRY);
        return ticket;
    }

    @Test
    void chargesOnlyBaseFeeWithinGracePeriod() {
        Ticket ticket = ticketOnSpot(SpotType.COMPACT);
        BigDecimal fee = strategy.calculateFee(ticket, ENTRY.plus(Duration.ofMinutes(10)));
        assertThat(fee).isEqualByComparingTo("5");
    }

    @Test
    void roundsPartialHourUp() {
        Ticket ticket = ticketOnSpot(SpotType.COMPACT);
        // 16 minutes -> over grace, rounds up to 1 hour: base 5 + 1*20
        BigDecimal fee = strategy.calculateFee(ticket, ENTRY.plus(Duration.ofMinutes(16)));
        assertThat(fee).isEqualByComparingTo("25");
    }

    @Test
    void chargesPerWholeHour() {
        Ticket ticket = ticketOnSpot(SpotType.LARGE);
        // 2h1m -> 3 billable hours: base 5 + 3*30
        BigDecimal fee = strategy.calculateFee(ticket, ENTRY.plus(Duration.ofHours(2)).plus(Duration.ofMinutes(1)));
        assertThat(fee).isEqualByComparingTo("95");
    }

    @Test
    void exactlyOneHourChargesOneHour() {
        Ticket ticket = ticketOnSpot(SpotType.COMPACT);
        BigDecimal fee = strategy.calculateFee(ticket, ENTRY.plus(Duration.ofHours(1)));
        assertThat(fee).isEqualByComparingTo("25");
    }

    @Test
    void rejectsExitBeforeEntry() {
        Ticket ticket = ticketOnSpot(SpotType.COMPACT);
        assertThatThrownBy(() -> strategy.calculateFee(ticket, ENTRY.minusSeconds(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

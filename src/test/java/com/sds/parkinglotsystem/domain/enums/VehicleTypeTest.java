package com.sds.parkinglotsystem.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleTypeTest {

    @Test
    void carPrefersCompactBeforeLarge() {
        assertThat(VehicleType.CAR.compatibleSpotTypesByPreference())
                .containsExactly(SpotType.COMPACT, SpotType.LARGE);
    }

    @Test
    void motorcycleFitsEverythingSmallestFirst() {
        assertThat(VehicleType.MOTORCYCLE.compatibleSpotTypesByPreference())
                .containsExactly(SpotType.MOTORCYCLE, SpotType.COMPACT, SpotType.LARGE);
    }

    @Test
    void truckOnlyFitsLarge() {
        assertThat(VehicleType.TRUCK.compatibleSpotTypesByPreference())
                .containsExactly(SpotType.LARGE);
        assertThat(VehicleType.TRUCK.canPark(SpotType.COMPACT)).isFalse();
        assertThat(VehicleType.TRUCK.canPark(SpotType.LARGE)).isTrue();
    }

    @Test
    void electricPrefersElectricSpot() {
        assertThat(VehicleType.ELECTRIC.compatibleSpotTypesByPreference().get(0))
                .isEqualTo(SpotType.ELECTRIC);
    }
}

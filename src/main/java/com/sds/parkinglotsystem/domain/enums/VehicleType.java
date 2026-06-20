package com.sds.parkinglotsystem.domain.enums;

import java.util.List;

/**
 * Kind of vehicle requesting parking. Each type declares the spot types it can
 * occupy, ordered by preference (best fit first). The assignment strategy walks
 * this list in order, so a CAR will take a COMPACT spot before consuming a LARGE
 * one.
 */
public enum VehicleType {

    MOTORCYCLE(List.of(SpotType.MOTORCYCLE, SpotType.COMPACT, SpotType.LARGE)),
    CAR(List.of(SpotType.COMPACT, SpotType.LARGE)),
    ELECTRIC(List.of(SpotType.ELECTRIC, SpotType.COMPACT, SpotType.LARGE)),
    TRUCK(List.of(SpotType.LARGE));

    private final List<SpotType> compatibleSpotTypes;

    VehicleType(List<SpotType> compatibleSpotTypes) {
        this.compatibleSpotTypes = compatibleSpotTypes;
    }

    /**
     * Spot types this vehicle can use, in best-fit preference order.
     */
    public List<SpotType> compatibleSpotTypesByPreference() {
        return compatibleSpotTypes;
    }

    public boolean canPark(SpotType spotType) {
        return compatibleSpotTypes.contains(spotType);
    }
}

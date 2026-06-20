package com.sds.parkinglotsystem.domain.enums;

/**
 * Physical category of a parking spot. The {@code size} rank is used by
 * best-fit assignment so that smaller vehicles prefer the tightest spot that
 * still fits, leaving larger spots free for larger vehicles.
 */
public enum SpotType {

    MOTORCYCLE(1),
    COMPACT(2),
    ELECTRIC(2),
    LARGE(3),
    HANDICAPPED(2);

    private final int size;

    SpotType(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }
}

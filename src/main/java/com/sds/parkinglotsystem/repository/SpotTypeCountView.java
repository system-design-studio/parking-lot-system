package com.sds.parkinglotsystem.repository;

import com.sds.parkinglotsystem.domain.enums.SpotType;

/**
 * Projection used to report available-spot counts grouped by spot type.
 */
public interface SpotTypeCountView {
    SpotType getSpotType();

    long getCount();
}
